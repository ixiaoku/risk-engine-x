package risk.engine.crawler.monitor.log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
/**
 * @Author: X
 * @Date: 2025/3/9 22:45
 * @Version: 1.0
 */
public class SolanaMonitor {
    private static final String SOLANA_WS = "wss://api.mainnet-beta.solana.com";

    public void start() {
        try {
            WebSocketClient client = new WebSocketClient(new URI(SOLANA_WS)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Solana WebSocket 连接成功");
                    send("{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"logsSubscribe\",\"params\":[\"all\"]}");
                }

                @Override
                public void onMessage(String message) {
                    JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                    if (json.has("params")) {
                        JsonObject value = json.getAsJsonObject("params").getAsJsonObject("result").getAsJsonObject("value");
                        String signature = value.get("signature").getAsString();
                        long lamports = value.get("lamports").getAsLong();
                        double solAmount = lamports / 1_000_000_000.0;
                        System.out.println("Solana 新交易: " + signature + ", 金额: " + solAmount + " SOL");
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Solana WebSocket 关闭: " + reason);
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
}
