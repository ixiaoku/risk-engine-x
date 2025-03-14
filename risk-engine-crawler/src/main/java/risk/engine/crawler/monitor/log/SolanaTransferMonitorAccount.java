package risk.engine.crawler.monitor.log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: X
 * @Date: 2025/3/10 19:26
 * @Version: 1.0
 */
public class SolanaTransferMonitorAccount {
    private static final String SOLANA_WS = "wss://api.mainnet-beta.solana.com";
    private static final String SOLANA_RPC = "https://api.mainnet-beta.solana.com";
    private static final OkHttpClient client = new OkHttpClient();
    private static final BlockingQueue<String> signatureQueue = new LinkedBlockingQueue<>();
    private static final Map<String, Long> accountBalances = new HashMap<>(); // 缓存账户余额

//    public static void main(String[] args) {
//        SolanaTransferMonitorAccount monitor = new SolanaTransferMonitorAccount();
//        monitor.startWebSocket();
//        monitor.startProcessingThread();
//    }

    public void startWebSocket() {
        try {
            WebSocketClient wsClient = new WebSocketClient(new URI(SOLANA_WS)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Solana WebSocket 连接成功");
                    String subscription = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"logsSubscribe\",\"params\":[\"all\"]}";
                    send(subscription);
                }

                @Override
                public void onMessage(String message) {
                    try {
                        //System.out.println("原始日志消息: " + message);
                        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                        if (!json.has("params")) {
                            System.out.println("消息无 params 字段，可能是订阅确认，跳过");
                            return;
                        }

                        JsonObject value = json.getAsJsonObject("params").getAsJsonObject("result").getAsJsonObject("value");
                        String signature = value.get("signature").getAsString();
                        if (value.has("err") && !value.get("err").isJsonNull()) {
                            //System.out.println("交易失败，跳过: " + signature);
                            return;
                        }

                        //System.out.println("日志内容:");
                        boolean systemProgramInvoked = false;
                        for (JsonElement log : value.getAsJsonArray("logs")) {
                            String logMessage = log.getAsString();
                            //System.out.println("  - " + logMessage);

                            if (logMessage.contains("Program 11111111111111111111111111111111 invoke")) {
                                systemProgramInvoked = true;
                            } else if (systemProgramInvoked && logMessage.contains("Program log: Instruction: Transfer")) {
                                signatureQueue.put(signature);
                                //System.out.println("检测到 System Program 原生 SOL 转账，签名: " + signature);
                                break;
                            } else if (logMessage.contains("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA") && logMessage.contains("Transfer")) {
                                System.out.println("检测到 SPL Token 转账，跳过");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("WebSocket 消息处理失败: " + e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket 关闭: " + reason + "，尝试重连...");
                    try {
                        Thread.sleep(5000);
                        reconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            wsClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startProcessingThread() {
        new Thread(() -> {
            while (true) {
                try {
                    String signature = signatureQueue.take();
                    fetchTransactionDetails(signature);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void fetchTransactionDetails(String signature) {
        try {
            String requestBody = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTransaction\",\"params\":[\"" + signature + "\",{\"encoding\":\"jsonParsed\"}]}";
            Request request = new Request.Builder()
                    .url(SOLANA_RPC)
                    .post(okhttp3.RequestBody.create(requestBody, okhttp3.MediaType.get("application/json")))
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            //System.out.println("交易详情原始响应: " + responseBody);

            JsonObject result = JsonParser.parseString(responseBody).getAsJsonObject().getAsJsonObject("result");
            if (result == null) {
                //System.out.println("交易详情为空，可能是未确认交易");
                return;
            }

            long slot = result.get("slot").getAsLong();
            String txSignature = result.getAsJsonObject("transaction").getAsJsonArray("signatures").get(0).getAsString();
            JsonObject message = result.getAsJsonObject("transaction").getAsJsonObject("message");
            JsonObject meta = result.getAsJsonObject("meta");

            String sender = null, receiver = null;
            long amount = 0;
            for (JsonElement instr : message.getAsJsonArray("instructions")) {
                JsonObject instrObj = instr.getAsJsonObject();
                if (instrObj.get("programId").getAsString().equals("11111111111111111111111111111111")) {
                    JsonObject parsed = instrObj.getAsJsonObject("parsed");
                    if (parsed != null && parsed.get("type").getAsString().equals("transfer")) {
                        JsonObject info = parsed.getAsJsonObject("info");
                        sender = info.get("source").getAsString();
                        receiver = info.get("destination").getAsString();
                        amount = info.get("lamports").getAsLong();
                        break;
                    }
                }
            }

            if (amount <= 0) {
                System.out.println("转账金额为 0，跳过");
                return;
            }

            // 更新本地余额缓存
            long preBalanceSender = meta.getAsJsonArray("preBalances").get(0).getAsLong();
            long postBalanceSender = meta.getAsJsonArray("postBalances").get(0).getAsLong();
            int receiverIndex = message.getAsJsonArray("accountKeys").size() > 1 ? 1 : 0;
            long preBalanceReceiver = meta.getAsJsonArray("preBalances").get(receiverIndex).getAsLong();
            long postBalanceReceiver = meta.getAsJsonArray("postBalances").get(receiverIndex).getAsLong();
            accountBalances.put(sender, postBalanceSender);
            accountBalances.put(receiver, postBalanceReceiver);

            // 只打印大于 1 SOL 的交易
            if (amount > 1_000_000_000) {
                long fee = meta.get("fee").getAsLong();
                double solAmount = amount / 1_000_000_000.0;
                System.out.println("--- 新增大额 SOL 转账交易 ---");
                System.out.println("槽位 (slot): " + slot);
                System.out.println("交易签名 (signature): " + txSignature);
                System.out.println("发送者地址 (sender): " + sender);
                System.out.println("接收者地址 (receiver): " + receiver);
                System.out.println("转账金额 (amount): " + amount + " lamports (" + solAmount + " SOL)");
                System.out.println("交易费用 (fee): " + fee + " lamports");
                System.out.println("发送者交易前余额 (preBalanceSender): " + preBalanceSender + " lamports");
                System.out.println("发送者交易后余额 (postBalanceSender): " + postBalanceSender + " lamports");
                System.out.println("接收者交易前余额 (preBalanceReceiver): " + preBalanceReceiver + " lamports");
                System.out.println("接收者交易后余额 (postBalanceReceiver): " + postBalanceReceiver + " lamports");
                System.out.println("---------------------");
            } else {
                System.out.println("转账金额小于 1 SOL，跳过: " + amount + " lamports");
            }
        } catch (Exception e) {
            System.err.println("获取交易详情失败: " + e.getMessage());
        }
    }
}
