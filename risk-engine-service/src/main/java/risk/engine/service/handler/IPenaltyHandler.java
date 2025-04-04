package risk.engine.service.handler;

import risk.engine.db.entity.PenaltyRecordPO;
import risk.engine.dto.enums.PenaltyStatusEnum;

/**
 * @Author: X
 * @Date: 2025/3/16 22:06
 * @Version: 1.0
 */
public interface IPenaltyHandler {

    PenaltyStatusEnum doPenalty(PenaltyRecordPO record);

}
