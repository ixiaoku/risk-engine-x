package risk.engine.service.service;

import risk.engine.db.entity.PenaltyRecordPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
public interface IPenaltyRecordService {

    boolean batchInsert(List<PenaltyRecordPO> records);

    List<PenaltyRecordPO> selectExample(PenaltyRecordPO penaltyRecord);

    boolean updateByPrimaryKey(PenaltyRecordPO record);

}
