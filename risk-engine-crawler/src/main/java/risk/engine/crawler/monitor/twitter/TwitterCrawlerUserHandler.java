package risk.engine.crawler.monitor.twitter;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.CryptoUtils;
import risk.engine.db.entity.CrawlerTask;
import risk.engine.dto.constant.BlockChainConstant;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.CrawlerNoticeDTO;
import risk.engine.dto.enums.TwitterUserEnum;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/21 16:12
 * @Version: 1.0
 */
@Slf4j
@Component
public class TwitterCrawlerUserHandler {

    @Resource
    private ICrawlerTaskService crawlerTaskService;

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void start() throws Exception {
        List<CrawlerTask> crawlerTasks = new ArrayList<>();
        for (TwitterUserEnum user : TwitterUserEnum.values()) {
            String secretKey = CryptoUtils.getDesSecretKey();
            String bearerToken = CryptoUtils.desDecrypt(BlockChainConstant.TWITTER_TOKEN, secretKey);
            String result = getTweetsByUserId(user.getUserId(), bearerToken);
            if (StringUtils.isEmpty(result)) {
                continue;
            }
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("data");
            if (Objects.isNull(jsonArray)) {
                continue;
            }
            for (JsonElement jsonElement : jsonArray) {
                JsonObject data = jsonElement.getAsJsonObject();
                String title = data.get("text").getAsString();
                String createdAt = data.get("created_at").getAsString();
                String flowNo = data.get("id").getAsString();
                CrawlerNoticeDTO noticeDTO = new CrawlerNoticeDTO();
                noticeDTO.setFlowNo(flowNo);
                noticeDTO.setTitle(title);
                noticeDTO.setCreatedAt(createdAt);
                CrawlerTask crawlerTask = crawlerTaskService.getCrawlerTask(flowNo, CrawlerConstant.TWITTER_USER_RELEASE_LIST, JSON.toJSONString(noticeDTO));
                if (Objects.isNull(crawlerTask)) {
                    continue;
                }
                crawlerTasks.add(crawlerTask);
            }
        }
        crawlerTaskService.batchInsert(crawlerTasks);
    }

    // 用用户 ID 获取推文
    public static String getTweetsByUserId(String userId, String bearerToken) throws Exception {
        String tweetsUrl = "https://api.twitter.com/2/users/" + userId + "/tweets?max_results=10&tweet.fields=id,text,created_at";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tweetsUrl))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("返回详情： {}", JSON.toJSONString(response));
        if (response.statusCode() != 200) {
            throw new RuntimeException("获取推文失败，状态码: " + response.statusCode() + ", 响应: " + response.body());
        }
        return response.body();
    }

    // 新增方法：通过 username 获取 userId
    public static String getUserIdByUsername(String username, String bearerToken) throws Exception {
        String userUrl = "https://api.twitter.com/2/users/by/username/" + username;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userUrl))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            log.error("获取用户 {} 的 userId 失败，状态码: {}, 响应: {}", username, response.statusCode(), response.body());
            return null;
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject data = jsonObject.getAsJsonObject("data");
        if (Objects.isNull(data)) {
            log.warn("用户 {} 的数据为空", username);
            return null;
        }
        return data.get("id").getAsString();
    }
}