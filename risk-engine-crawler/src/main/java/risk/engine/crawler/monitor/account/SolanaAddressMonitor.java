package risk.engine.crawler.monitor.account;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

/**
 * @Author: X
 * @Date: 2025/3/12 11:30
 * @Version: 1.0
 */
public class SolanaAddressMonitor {
    private static final String SOLANA_WS = "wss://api.mainnet-beta.solana.com";
    private static final String TARGET_ADDRESS = "5tzFkiKscXHK5ZXCGbXZxdw7gTjjD1mBwuoFbhUvuAi9"; // 替换为目标地址

//    public static void main(String[] args) {
//        new SolanaAddressMonitor().startMonitoring();
//    }

    public void startMonitoring() {
        try {
            WebSocketClient wsClient = new WebSocketClient(new URI(SOLANA_WS)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Solana WebSocket 连接成功");
                    // 订阅账户变化
                    String accountSub = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"accountSubscribe\",\"params\":[\"" + TARGET_ADDRESS + "\",{\"encoding\":\"jsonParsed\"}]}";
                    send(accountSub);
                    // 订阅日志
                    String logsSub = "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"logsSubscribe\",\"params\":[{\"mentions\":[\"" + TARGET_ADDRESS + "\"]},{\"commitment\":\"confirmed\"}]}";
                    send(logsSub);
                }

                @Override
                public void onMessage(String message) {
                    JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                    if (!json.has("params")) return;

                    JsonObject params = json.getAsJsonObject("params");
                    if (json.get("method").getAsString().equals("accountNotification")) {
                        long lamports = params.getAsJsonObject("result").getAsJsonObject("value").getAsJsonObject("account").get("lamports").getAsLong();
                        System.out.println("地址 " + TARGET_ADDRESS + " 余额变化: " + lamports + " lamports");
                    } else if (json.get("method").getAsString().equals("logsNotification")) {
                        String signature = params.getAsJsonObject("result").getAsJsonObject("value").get("signature").getAsString();
                        System.out.println("地址 " + TARGET_ADDRESS + " 涉及交易签名: " + signature);
                        // 可选：调用 HTTP 获取详情
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket 关闭: " + reason + "，尝试重连...");
                    try { Thread.sleep(5000); reconnect(); } catch (Exception e) { e.printStackTrace(); }
                }

                @Override
                public void onError(Exception ex) { ex.printStackTrace(); }
            };
            wsClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
