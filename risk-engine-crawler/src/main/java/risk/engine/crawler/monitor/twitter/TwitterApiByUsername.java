package risk.engine.crawler.monitor.twitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import risk.engine.common.util.CryptoUtils;
import risk.engine.dto.constant.BlockChainConstant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @Author: X
 * @Date: 2025/3/21 16:12
 * @Version: 1.0
 */
@Slf4j
public class TwitterApiByUsername {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)) // 连接超时 10 秒
            .build();
    private static final ObjectMapper mapper = new ObjectMapper(); // 用于解析 JSON

    public static void main(String[] args) {
        String secretKey = CryptoUtils.getDesSecretKey();
        String bearerToken = CryptoUtils.desDecrypt(BlockChainConstant.TWITTER_TOKEN, secretKey);
        String username = "1483495485889564674"; // 目标用户名
        try {
            String tweets = getTweetsByUserId(username, bearerToken);
            System.out.println("推文结果: " + tweets);
        } catch (Exception e) {
            log.error("错误信息：{}", e.getMessage(), e);
        }
    }

    // 用用户 ID 获取推文
    public static String getTweetsByUserId(String userId, String bearerToken) throws Exception {
        String tweetsUrl = "https://api.twitter.com/2/users/" + userId + "/tweets?max_results=10&tweet.fields=id,text,created_at";
        HttpRequest tweetsRequest = HttpRequest.newBuilder()
                .uri(URI.create(tweetsUrl))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15)) // 请求超时 15 秒
                .GET()
                .build();
        HttpResponse<String> tweetsResponse = sendWithRetry(tweetsRequest, 3);
        if (tweetsResponse.statusCode() != 200) {
            throw new RuntimeException("获取推文失败，状态码: " + tweetsResponse.statusCode() + ", 响应: " + tweetsResponse.body());
        }
        return tweetsResponse.body();
    }

    // 发送请求并带重试逻辑
    private static HttpResponse<String> sendWithRetry(HttpRequest request, int maxRetries) throws Exception {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                System.out.println("发送请求到: " + request.uri());
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response;
            } catch (java.io.IOException e) {
                attempt++;
                if (attempt == maxRetries) {
                    throw e; // 达到最大重试次数，抛出异常
                }
                System.out.println("连接失败，第 " + attempt + " 次重试...");
                Thread.sleep(1000 * attempt); // 每次重试延迟增加
            }
        }
        throw new RuntimeException("重试次数耗尽，请求失败");
    }
}
