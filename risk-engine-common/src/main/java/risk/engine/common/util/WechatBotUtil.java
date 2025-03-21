package risk.engine.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.GroupChatBotDTO;

/**
 * 微信企业群bot机器人
 * @Author: X
 * @Date: 2025/3/21 16:37
 * @Version: 1.0
 */
@Slf4j
public class WechatBotUtil {

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static Boolean send(String content) {
        try {
            //解密这个api token
            String secretKey = CryptoUtils.getDesSecretKey();
            String key = CryptoUtils.desDecrypt(CrawlerConstant.weChatBotDataKey, secretKey);
            GroupChatBotDTO groupChatBotDTO = new GroupChatBotDTO();
            groupChatBotDTO.setMsgtype("markdown");
            GroupChatBotDTO.Markdown markdown = new GroupChatBotDTO.Markdown();
            markdown.setContent(content);
            groupChatBotDTO.setMarkdown(markdown);
            String result = OkHttpUtil.post(CrawlerConstant.weChatBotUrl + key, gson.toJson(groupChatBotDTO));
            if (StringUtils.isEmpty(result)) {
                log.error("企业微信群bot 消息发送失败： {}", result);
                return Boolean.FALSE;
            }
            JsonObject resultObject = JsonParser.parseString(result).getAsJsonObject();
            if (StringUtils.isNotEmpty(resultObject.get("code").getAsString()) && CrawlerConstant.notIceCode.equals(resultObject.get("code").getAsString())) {
                log.info("机器人发消息成功: {}", result);
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (Exception e) {
            log.error("企业微信 BOT发消息失败：{}", e.getMessage(), e);
            throw new RuntimeException("企业微信 BOT发消息失败:" + e);
        }
    }

}
