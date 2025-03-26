package risk.engine.crawler.monitor.binance;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.CryptoUtils;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.common.wechat.base.MessageApi;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.GroupChatBotDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 币安公告监控
 */
@Slf4j
@Component
public class MarketNoticeMonitorHandler {

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public void start() {

        // 发送 HTTP GET 请求
        String jsonResponse = OkHttpUtil.get(CrawlerConstant.notIceUrl);
        if (StringUtils.isEmpty(jsonResponse)) {
            log.error("请求失败，未获取到数据");
            return;
        }
        // 解析json
        extractTitles(jsonResponse);
    }

    /**
     * 使用 Gson 解析 JSON 并提取 title 字段
     * @param jsonResponse 参数
     */
    private void extractTitles(String jsonResponse) {
        JsonObject rootObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        // 检查返回状态
        String code = rootObject.get("code").getAsString();
        if (!"000000".equals(code)) {
            log.error("API 返回错误，code: {}, message: {}", code, rootObject.get("message"));
            return;
        }
        JsonObject data = rootObject.get("data").getAsJsonObject();
        // 获取 data 数组
        JsonArray catalogs = data.getAsJsonArray("catalogs");
        if (Objects.isNull(catalogs)) {
            log.error("未找到 data 数组");
        }
        int i = 0;
        //解密这个api token
        String secretKey = CryptoUtils.getDesSecretKey();
        String key = CryptoUtils.desDecrypt(CrawlerConstant.weChatBotDataKey, secretKey);
        for (JsonElement element : catalogs) {
            JsonObject dataObject = element.getAsJsonObject();
            JsonArray articles =  dataObject.get("articles").getAsJsonArray();
            for (JsonElement article : articles) {
                JsonObject articleObject = article.getAsJsonObject();
                String title = articleObject.get("title").getAsString();
                String time = DateTimeUtil.getTimeByTimestamp(articleObject.get("releaseDate").getAsLong());
                String content = String.format(CrawlerConstant.notIceBotContent, title, time);
                log.info("标题: {}", title);
                GroupChatBotDTO groupChatBotDTO = new GroupChatBotDTO();
                groupChatBotDTO.setMsgtype("markdown");
                GroupChatBotDTO.Markdown markdown = new GroupChatBotDTO.Markdown();
                markdown.setContent(content);
                groupChatBotDTO.setMarkdown(markdown);
                //企业微信群bot
//                String result = OkHttpUtil.post(CrawlerConstant.weChatBotUrl + key, gson.toJson(groupChatBotDTO));
//                if (StringUtils.isEmpty(result)) {
//                    log.error("企业微信群bot 消息发送失败： {}", result);
//                }
                // 个人微信
                String textContent = String.format(CrawlerConstant.PERSON_BOT_CONTENT, title, time );
                send(textContent);
                i++;
                if (i > 1) {
                    return;
                }
            }
        }
    }

    public void send(String content) {
        List<String> toWxids = Arrays.asList("44760028169@chatroom", "52067326265@chatroom", "48977305404@chatroom");
        for (String toWxid : toWxids) {
            MessageApi.postText(CrawlerConstant.appId, toWxid,content,"");
            int i = new Random().nextInt(6);
            try {
                Thread.sleep(i*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}