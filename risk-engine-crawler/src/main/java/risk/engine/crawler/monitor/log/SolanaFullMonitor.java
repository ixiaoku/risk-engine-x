package risk.engine.crawler.monitor.log;

/**
 * @Author: X
 * @Date: 2025/3/10 00:49
 * @Version: 1.0
 */
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SolanaFullMonitor {
    private static final String SOLANA_WS = "wss://api.mainnet-beta.solana.com";
    private static final String SOLANA_RPC = "https://api.mainnet-beta.solana.com";
    private static final OkHttpClient client = new OkHttpClient();
    private static final BlockingQueue<String> signatureQueue = new LinkedBlockingQueue<>();

//    public static void main(String[] args) {
//
//        SolanaFullMonitor monitor = new SolanaFullMonitor();
//
//        //monitor.reconnect();
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
                    String s = "{\n" +
                            "  \"jsonrpc\": \"2.0\",\n" +
                            "  \"id\": 1,\n" +
                            "  \"method\": \"logsSubscribe\",\n" +
                            "  \"params\": [\n" +
                            "    {\n" +
                            "      \"mentions\": [ \"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\" ]\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"commitment\": \"confirmed\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
                    send(s);
                }

                @Override
                public void onMessage(String message) {
                    try {
                        //System.out.println("原始日志消息: " + message);
                        LogsNotificationResponse jsonResponse = new Gson().fromJson(message, LogsNotificationResponse.class);

                        if (Objects.isNull(jsonResponse.getParams())) {
                            System.out.println("消息无 params 字段，跳过");
                            return;
                        }

                        LogsNotificationResponse.Params.Result.Value value = jsonResponse.getParams().getResult().getValue();
                        String signature = value.getSignature();

                        Object err = value.getErr();
                        if (ObjectUtils.isNotEmpty(err)) {
                            return;
                        }

                        //System.out.println("日志内容:");
                        boolean systemProgramInvoked = false;
                        for (String logMessage : value.getLogs()) {
                            //System.out.println("  - " + logMessage);
//                            if(logMessage.contains("Program log: Instruction: Transfer")) {
//                                //System.out.println("11检测到 System Program 原生 SOL 转账，签名: " + signature);
//                                signatureQueue.put(signature);
//                                break;
//                            }
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
            String requestBody = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTransaction\",\"params\":[\"" + signature + "\",{\"encoding\":\"json\"}]}";
            String requestStr = "{\n" +
                    "  \"jsonrpc\":\"2.0\",\n" +
                    "  \"id\":1,\n" +
                    "  \"method\":\"getTransaction\",\n" +
                    "  \"params\":[\n" +
                    "    \"2CxNRsyRT7y88GBwvAB3hRg8wijMSZh3VNYXAdUesGSyvbRJbRR2q9G1KSEpQENmXHmmMLHiXumw4dp8CvzQMjrM\",\n" +
                    "    {\n" +
                    "      \"encoding\":\"jsonParsed\",\n" +
                    "      \"maxSupportedTransactionVersion\":0\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            Request request = new Request.Builder()
                    .url(SOLANA_RPC)
                    .post(okhttp3.RequestBody.create(requestStr, okhttp3.MediaType.get("application/json")))
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("交易详情原始响应: " + responseBody);

            JsonObject result = JsonParser.parseString(responseBody).getAsJsonObject().getAsJsonObject("result");
            if (result == null) {
                //System.out.println("交易详情为空，可能是未确认交易");
                return;
            }

            long slot = result.get("slot").getAsLong();
            String txSignature = result.getAsJsonObject("transaction").getAsJsonArray("signatures").get(0).getAsString();
            JsonObject message = result.getAsJsonObject("transaction").getAsJsonObject("message");
            String sender = message.getAsJsonArray("accountKeys").get(0).getAsString();
            String receiver = message.getAsJsonArray("accountKeys").get(1).getAsString();
            long fee = result.getAsJsonObject("meta").get("fee").getAsLong();
            long preBalanceSender = result.getAsJsonObject("meta").getAsJsonArray("preBalances").get(0).getAsLong();
            long postBalanceSender = result.getAsJsonObject("meta").getAsJsonArray("postBalances").get(0).getAsLong();
            long preBalanceReceiver = result.getAsJsonObject("meta").getAsJsonArray("preBalances").get(1).getAsLong();
            long postBalanceReceiver = result.getAsJsonObject("meta").getAsJsonArray("postBalances").get(1).getAsLong();
            long amount = preBalanceSender - postBalanceSender - fee;
            double solAmount = amount / 1_000_000_000.0;
            if (amount <= 0) {
                System.out.println("转账金额为 0 或未找到 Transfer 指令，跳过");
                return;
            }

            System.out.println("--- 新增 SOL 转账交易 ---");
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
        } catch (Exception e) {
            System.err.println("获取交易详情失败: " + e.getMessage());
        }
    }

    public void reconnect() {
        String message = "{\"jsonrpc\":\"2.0\",\"method\":\"logsNotification\",\"params\":{\"result\":{\"context\":{\"slot\":325665489},\"value\":{\"signature\":\"31s9SnXDkJjzAJw9NzmYrSZTejnHz9sxJGDtntdbHfMVKJPcFK6Es8E5wpWcLSGPzbLgDZtLd52riqknRkbhT1Bu\",\"err\":null,\"logs\":[\"Program 11111111111111111111111111111111 invoke [1]\",\"Program 11111111111111111111111111111111 success\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA invoke [1]\",\"Program log: Instruction: InitializeAccount\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA consumed 3443 of 799850 compute units\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA success\",\"Program 675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8 invoke [1]\",\"Program log: ray_log: A0TchwEAAAAA/FrYAAAAAAACAAAAAAAAAAtLXh6kCAAA6aUIoTEAAAAC2K8UNwAAAFzEsQEAAAAA\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA invoke [2]\",\"Program log: Instruction: Transfer\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA consumed 4645 of 777705 compute units\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA success\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA invoke [2]\",\"Program log: Instruction: Transfer\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA consumed 4736 of 770079 compute units\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA success\",\"Program 675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8 consumed 32142 of 796407 compute units\",\"Program 675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8 success\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA invoke [1]\",\"Program log: Instruction: CloseAccount\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA consumed 2915 of 764265 compute units\",\"Program TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA success\"]}},\"subscription\":567004}}";
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        if (!json.has("params")) {
            System.out.println("消息无 params 字段，跳过");
            return;
        }

        JsonObject value = json.getAsJsonObject("params").getAsJsonObject("result").getAsJsonObject("value");
        String signature = value.get("signature").getAsString();

        JsonNull err = value.get("err").getAsJsonNull();
        if (!err.isJsonNull()) {
            return;
        }

        //System.out.println("日志内容:");
        boolean systemProgramInvoked = false;
        for (JsonElement log : value.getAsJsonArray("logs")) {
            String logMessage = log.getAsString();
            //System.out.println("  - " + logMessage);
            if(logMessage.contains("Program log: Instruction: Transfer")) {
                System.out.println("11检测到 System Program 原生 SOL 转账，签名: " + signature);
                System.out.println("成功");
                break;
            }
            if (logMessage.contains("Program 11111111111111111111111111111111 invoke")) {
                systemProgramInvoked = true;
            } else if (systemProgramInvoked && logMessage.contains("Program log: Instruction: Transfer")) {
                //signatureQueue.put(signature);
                System.out.println("检测到 System Program 原生 SOL 转账，签名: " + signature);
                break;
            } else if (logMessage.contains("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA") && logMessage.contains("Transfer")) {
                System.out.println("检测到 SPL Token 转账，跳过");
                break;
            }
        }
    }
}