package risk.engine.crawler.monitor;

/**
 * @Author: X
 * @Date: 2025/3/21 15:22
 * @Version: 1.0
 */
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class TwitterApiExampleHttpClient {
    private static final String BEARER_TOKEN = ""; // 替换为你的 Bearer Token
    private static final String USER_ID = "44196397"; // 替换为目标用户 ID

    public static void main(String[] args) {
        try {
            // 创建 HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // 构建请求 URL
            String apiUrl = "https://api.twitter.com/2/users/" + USER_ID + "/tweets?max_results=5&tweet.fields=id,text,created_at";

            String apiUsernameUrl = "https://api.twitter.com/2/users/by/username/cz_binance";

            // 创建 HTTP 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUsernameUrl))
                    .header("Authorization", "Bearer " + BEARER_TOKEN)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            System.out.println("Sending request to " + apiUrl + "...");
            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 输出结果
            System.out.println("响应码: " + response.statusCode());
            System.out.println("返回结果: " + response.body());

        } catch (Exception e) {
            log.error("错误信息： {}", e.getMessage(), e);
        }
    }
}
