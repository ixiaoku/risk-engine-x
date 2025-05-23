package risk.engine.service.handler.penalty;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.CryptoUtils;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.db.entity.PenaltyRecordPO;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.GroupChatBotDTO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.service.handler.IPenaltyHandler;

import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/26 22:21
 * @Version: 1.0
 */
@Slf4j
@Component
public class BusinessWechatHandler implements IPenaltyHandler {

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Override
    public PenaltyStatusEnum doPenalty(PenaltyRecordPO record) {
        try {
            //解密这个api token
            AnnouncementDTO announcement = JSONObject.parseObject(record.getPenaltyJson(), AnnouncementDTO.class);
            if (Objects.isNull(announcement)) {
                throw new RuntimeException("处置报文为空");
            }
            String secretKey = CryptoUtils.getDesSecretKey();
            String key = CryptoUtils.desDecrypt(CrawlerConstant.weChatBotDataKey, secretKey);
            String content = String.format(CrawlerConstant.noticeBotContent, announcement.getTitle(), announcement.getContent(), announcement.getCreatedAt());
            //组装企业微信 markdown格式报文
            GroupChatBotDTO groupChatBotDTO = new GroupChatBotDTO();
            groupChatBotDTO.setMsgtype("markdown");
            GroupChatBotDTO.Markdown markdown = new GroupChatBotDTO.Markdown();
            markdown.setContent(content);
            groupChatBotDTO.setMarkdown(markdown);
            String result = OkHttpUtil.postJson(CrawlerConstant.weChatBotUrl + key, gson.toJson(groupChatBotDTO));
            if (StringUtils.isEmpty(result)) {
                log.error("企业微信群bot 消息发送失败： {}", result);
                return PenaltyStatusEnum.FAIL;
            }
            return PenaltyStatusEnum.SUCCESS;
        } catch (Exception e) {
            log.error("企业微信 BOT发消息失败：{}", e.getMessage(), e);
            //服务不可用或者服务超时 重试三次
            return PenaltyStatusEnum.WAIT;
        }
    }

}
