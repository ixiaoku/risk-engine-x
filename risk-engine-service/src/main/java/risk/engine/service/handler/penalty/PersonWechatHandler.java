package risk.engine.service.handler.penalty;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.wechat.base.MessageApi;
import risk.engine.db.entity.PenaltyRecord;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.service.handler.IPenaltyHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @Author: X
 * @Date: 2025/3/26 22:21
 * @Version: 1.0
 */
@Slf4j
@Component
public class PersonWechatHandler implements IPenaltyHandler {

    public PenaltyStatusEnum doPenalty (PenaltyRecord record) {

        try {
            JSONObject articleObject = JSONObject.parseObject(record.getPenaltyJson());
            String title = articleObject.getString("title");
            String time = DateTimeUtil.getTimeByTimestamp(articleObject.getLong("releaseDate"));
            String textContent = String.format(CrawlerConstant.PERSON_BOT_CONTENT, title, time );
            List<String> toWechatIds = Arrays.asList("44760028169@chatroom", "52067326265@chatroom", "48977305404@chatroom");
            for (String toWechatId : toWechatIds) {
                MessageApi.postText(CrawlerConstant.appId, toWechatId, textContent,"");
                int i = new Random().nextInt(6);
                try {
                    Thread.sleep(i*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return PenaltyStatusEnum.SUCCESS;
        } catch (RuntimeException e) {
            log.error("个人微信 BOT发消息失败：{}", e.getMessage(), e);
            return PenaltyStatusEnum.WAIT;
        }
    }
}
