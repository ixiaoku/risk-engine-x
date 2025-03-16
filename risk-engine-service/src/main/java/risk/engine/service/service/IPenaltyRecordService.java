package risk.engine.service.service;

import risk.engine.db.entity.Penalty;
import risk.engine.db.entity.PenaltyRecord;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
public interface IPenaltyRecordService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(PenaltyRecord record);

    Penalty selectByPrimaryKey(Long id);

    List<PenaltyRecord> selectExample(PenaltyRecord penaltyRecord);

    boolean updateByPrimaryKey(PenaltyRecord record);

}
