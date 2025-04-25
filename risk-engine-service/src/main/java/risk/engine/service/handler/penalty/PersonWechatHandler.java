package risk.engine.service.handler.penalty;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.db.entity.PenaltyRecordPO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.service.handler.IPenaltyHandler;

/**
 * @Author: X
 * @Date: 2025/3/26 22:21
 * @Version: 1.0
 */
@Slf4j
@Component
public class PersonWechatHandler implements IPenaltyHandler {

    public PenaltyStatusEnum doPenalty (PenaltyRecordPO record) {
        try {
            AnnouncementDTO announcementDTO = JSONObject.parseObject(record.getPenaltyJson(), AnnouncementDTO.class);
            OkHttpUtil.postJson("htpp://106.55.233.79:8090/gewe/sendMsg", JSONObject.toJSONString(announcementDTO));
            return PenaltyStatusEnum.SUCCESS;
        } catch (RuntimeException e) {
            log.error("个人微信 BOT发消息失败：{}", e.getMessage(), e);
            return PenaltyStatusEnum.WAIT;
        }
    }
}
