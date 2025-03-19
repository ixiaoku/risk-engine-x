package risk.engine.crawler.monitor;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.GroupChatBotDTO;

import java.util.Objects;

@Slf4j
@Component
public class BinanceCrawlerHandler {

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public void start() {

        // 发送 HTTP GET 请求
        String jsonResponse = OkHttpUtil.get(CrawlerConstant.notIceUrl);
        if (StringUtils.isEmpty(jsonResponse)) {
            log.error("请求失败，未获取到数据");
        }
        // 解析json
        extractTitles(jsonResponse);
    }

    // 使用 Gson 解析 JSON 并提取 title 字段
    private static void extractTitles(String jsonResponse) {
        JsonObject rootObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        // 检查返回状态
        String code = rootObject.get("code").getAsString();
        if (!"000000".equals(code)) {
            log.error("API 返回错误，code: {}, message: {}", code, rootObject.get("message"));
            return;
        }
        // 获取 data 数组
        JsonArray dataArray = rootObject.getAsJsonArray("data");
        if (Objects.isNull(dataArray)) {
            log.error("未找到 data 数组");
        }

        for (JsonElement element : dataArray) {
            JsonObject dataObject = element.getAsJsonObject();
            String title = dataObject.get("title").getAsString();
            String time = DateTimeUtil.getTimeByTimestamp(dataObject.get("time").getAsLong());
            String type = dataObject.get("type").getAsString();
            String content = String.format(CrawlerConstant.notIceBotContent, title, time);
            if (title != null && CrawlerConstant.notIceType.equals(type)) {
                log.info("标题: {}", title);
                GroupChatBotDTO groupChatBotDTO = new GroupChatBotDTO();
                groupChatBotDTO.setMsgtype("markdown");
                GroupChatBotDTO.Markdown markdown = new GroupChatBotDTO.Markdown();
                markdown.setContent(content);
                groupChatBotDTO.setMarkdown(markdown);
                //企业微信群bot
                String result = OkHttpUtil.post(CrawlerConstant.weChatBotUrl, gson.toJson(groupChatBotDTO));
                if (StringUtils.isEmpty(result)) {
                    log.error("企业微信群bot 消息发送失败： {}", result);
                }
                JsonObject resultObject = JsonParser.parseString(result).getAsJsonObject();
                if (StringUtils.isNotEmpty(resultObject.get("code").getAsString()) && CrawlerConstant.notIceCode.equals(resultObject.get("code").getAsString())) {
                    log.info("机器人发消息成功: {}", result);
                }
            }
        }
    }
}