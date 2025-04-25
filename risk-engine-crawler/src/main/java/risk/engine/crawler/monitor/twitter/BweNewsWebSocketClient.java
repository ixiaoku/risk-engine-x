package risk.engine.crawler.monitor.twitter;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

/**
 * @Author: X
 * @Date: 2025/4/25 13:46
 * @Version: 1.0
 */

public class BweNewsWebSocketClient extends WebSocketClient {

    public BweNewsWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("✅ Connected to BWEnews WebSocket.");
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String source = json.optString("source_name");
            String title = json.optString("news_title");
            String url = json.optString("url");
            long timestamp = json.optLong("timestamp");

            System.out.printf("📢 [%s] %s\n🔗 %s\n⏰ %d\n", source, title, url, timestamp);
        } catch (Exception e) {
            System.err.println("⚠️ Error parsing message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ Connection closed. Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("🚨 WebSocket Error: " + ex.getMessage());
    }

    public static void main(String[] args) {
        try {
            URI uri = new URI("wss://bwenews-api.bwe-ws.com/ws");
            BweNewsWebSocketClient client = new BweNewsWebSocketClient(uri);
            client.connect();

            // 防止主线程退出
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
