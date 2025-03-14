package risk.engine.crawler.monitor.log;

/**
 * @Author: X
 * @Date: 2025/3/9 23:31
 * @Version: 1.0
 */
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class SolanaIncrementMonitor {
    private static final String SOLANA_WS = "wss://api.mainnet-beta.solana.com";
    private static final String SYSTEM_PROGRAM = "11111111111111111111111111111111";
    private static final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

//    public static void main(String[] args) {
//        SolanaIncrementMonitor monitor = new SolanaIncrementMonitor();
//        monitor.startWebSocket();
//        monitor.startProcessingThread();
//    }

    public void startWebSocket() {
        try {
            WebSocketClient client = new WebSocketClient(new URI(SOLANA_WS)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Solana WebSocket 连接成功");
                    String subscription = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"programSubscribe\",\"params\":[\"" + SYSTEM_PROGRAM + "\",{\"encoding\":\"jsonParsed\"}]}";
                    send(subscription);
                }

                @Override
                public void onMessage(String message) {
                    try {
                        messageQueue.put(message); // 将消息放入队列
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startProcessingThread() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = messageQueue.take(); // 从队列中取出消息
                    parseTransaction(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseTransaction(String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            System.out.println("parseTransaction: " + new Gson().toJson(json));
            if (!json.has("params")) return;

            JsonObject result = json.getAsJsonObject("params").getAsJsonObject("result").getAsJsonObject("value");
            JsonObject account = result.getAsJsonObject("account");
            JsonObject parsedData = account.getAsJsonObject("data").getAsJsonObject("parsed");

            // 只处理转账交易
            if (!parsedData.has("info") || !parsedData.getAsJsonObject("info").has("lamports")) return;

            JsonObject info = parsedData.getAsJsonObject("info");

            // 提取所有字段
            String signature = result.get("signature").getAsString();
            long slot = result.get("slot") != null ? result.get("slot").getAsLong() : -1;
            String source = info.get("source").getAsString();
            String destination = info.get("destination").getAsString();
            long lamports = info.get("lamports").getAsLong();
            double solAmount = lamports / 1_000_000_000.0;
            String programId = parsedData.get("programId").getAsString();
            String type = parsedData.get("type").getAsString();
            long space = account.get("space").getAsLong();
            boolean executable = account.get("executable").getAsBoolean();
            long rentEpoch = account.get("rentEpoch").getAsLong();
            JsonArray owner = account.get("owner") != null ? account.getAsJsonArray("owner") : null;

            // 输出所有字段
            System.out.println("--- 新增 SOL 转账交易 ---");
            System.out.println("交易签名 (signature): " + signature);
            System.out.println("槽位 (slot): " + slot);
            System.out.println("发送者地址 (source): " + source);
            System.out.println("接收者地址 (destination): " + destination);
            System.out.println("转账金额 (lamports): " + lamports + " (" + solAmount + " SOL)");
            System.out.println("程序 ID (programId): " + programId);
            System.out.println("指令类型 (type): " + type);
            System.out.println("账户空间 (space): " + space);
            System.out.println("是否可执行 (executable): " + executable);
            System.out.println("租金周期 (rentEpoch): " + rentEpoch);
            System.out.println("拥有者 (owner): " + (owner != null ? owner.toString() : "N/A"));
            System.out.println("---------------------");
        } catch (Exception e) {
            System.err.println("解析失败: " + e.getMessage());
        }
    }
}