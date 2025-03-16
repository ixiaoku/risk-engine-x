package risk.engine.service.service;

import risk.engine.db.entity.Penalty;
import risk.engine.db.entity.PenaltyRecord;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
public interface IPenaltyRecordService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(PenaltyRecord record);

    Penalty selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(PenaltyRecord record);

}
