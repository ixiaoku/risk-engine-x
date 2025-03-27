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
 * 推文结果: {"data":[{"created_at":"2025-03-26T15:45:25.000Z","text":"BWENEWS: Hyperliquid in discord: \"After evidence of suspicious market activity, the validator set convened and voted to delist JELLY perps, @here.\n\nAll users apart from flagged addresses will be made whole from the Hyper Foundation. This will be done automatically in the coming","edit_history_tweet_ids":["1904922647252918359"],"id":"1904922647252918359"},{"created_at":"2025-03-26T15:28:24.000Z","text":"BWENEWS: Hyperliquid delisted $JELLYJELLY and settled the position at a favorable price (0.0095$) without losing any money -SigmaSquerd\n\n方程式新闻: Hyperliquid 下架 $JELLYJELLY 并以优惠价格（0.0095 美元）结算，没有损失任何资金 -SigmaSquerd https://t.co/QjCdR7bx4m","edit_history_tweet_ids":["1904918366575616228"],"id":"1904918366575616228"},{"created_at":"2025-03-26T15:21:04.000Z","text":"BWENEWS: Hyperliquid delists JELLYJELLY after Binance and OKX perpetual listing\n\n方程式新闻: Hyperliquid 在 Binance 和 OKX 永久上市后下架 JELLYJELLY","edit_history_tweet_ids":["1904916517659214136"],"id":"1904916517659214136"},{"created_at":"2025-03-26T15:16:13.000Z","text":"Binance EN: Binance Futures Will Launch USDⓈ-Margined JELLYJELLYUSDT and MAVIAUSDT Perpetual Contracts\n\n币安重要公告: 币安合约即将上线美元保证金 JELLYJELLYUSDT 和 MAVIAUSDT 永续合约\nhttps://t.co/7aEmPqhboH","edit_history_tweet_ids":["1904915300308705321"],"id":"1904915300308705321"},{"created_at":"2025-03-26T15:12:37.000Z","text":"BWENEWS: OKX to list perpetual futures for JELLYJELLY crypto https://t.co/wH9r5dqFiX\n\n方程式新闻: OKX 将上线 JELLYJELLY 加密货币永续期货","edit_history_tweet_ids":["1904914394154491931"],"id":"1904914394154491931"},{"created_at":"2025-03-26T14:51:55.000Z","text":"BWENEWS: Funny Twitter dialogue, no comment https://t.co/uBTvHPekgu\n\n方程式新闻: 搞笑推特对话，不评论","edit_history_tweet_ids":["1904909182828240938"],"id":"1904909182828240938"},{"created_at":"2025-03-26T13:39:29.000Z","text":"BWENEWS: After the traders tries to squeeze on JELLYJELLY, Hyperliquid's vault now in -$6m PnL on its short position. https://t.co/OhamI8UvKs\n\n方程式新闻: 在交易员试图挤压 JELLYJELLY 之后，Hyperliquid 的金库现在在其空头头寸上亏损了 600 万美元。","edit_history_tweet_ids":["1904890956614336583"],"id":"1904890956614336583"},{"created_at":"2025-03-26T13:16:10.000Z","text":"AggrNews: HYPERLIQUID VAULT TAKES A $5M SHORT POSITION ON JELLYJELLY AS TRADER SELF-LIQUIDATES HIMSELF https://t.co/i9muThlIUN\n\nAggrNews: HYPERLIQUID VAULT 在交易员自行清算的情况下对 JELLYJELLY 进行了 500 万美元的空头仓位","edit_history_tweet_ids":["1904885085570314574"],"id":"1904885085570314574"},{"created_at":"2025-03-26T12:03:01.000Z","text":"Bitget Listing: Bitget Will List Banana For Scale (BANANAS31) in the Innovation and Meme Zone!\n\nBitget上新: Bitget将在创新与Meme专区上线Banana For Scale（BANANAS31）！\nhttps://t.co/LqcvNNfirF\n\n$BANANAS31","edit_history_tweet_ids":["1904866679882211825"],"id":"1904866679882211825"},{"created_at":"2025-03-26T10:57:15.000Z","text":"⚠️BWENEWS: Satoshi Club: .@PancakeSwap now shows net $CAKE burn - how much was burned minus how much was minted. \n\nThis gives a better view of how the supply is actually changing.\n\nIn the latest update (March 24), about 339K CAKE was burned, cutting supply by 0.12%.\n\nThe","edit_history_tweet_ids":["1904850129468928453"],"id":"1904850129468928453"}],"meta":{"next_token":"7140dibdnow9c7btw4dznc64vazwcgsn2ef2rbhgh92cz","result_count":10,"newest_id":"1904922647252918359","oldest_id":"1904850129468928453"}}
 */
@Slf4j
@Component
public class TwitterCrawlerUserRelease {

    @Resource
    private ICrawlerTaskService crawlerTaskService;

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void send() throws Exception {
        List<CrawlerTask> crawlerTasks = new ArrayList<>();
        for (TwitterUserEnum user : TwitterUserEnum.values()) {
            String secretKey = CryptoUtils.getDesSecretKey();
            String bearerToken = CryptoUtils.desDecrypt(BlockChainConstant.TWITTER_TOKEN, secretKey);
            String result = getTweetsByUserId(user.getUsername(), bearerToken);
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