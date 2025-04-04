package risk.engine.service.service;

import risk.engine.db.entity.PenaltyActionPO;
import risk.engine.db.entity.PenaltyRecordPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
public interface IPenaltyRecordService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(PenaltyRecordPO record);

    boolean batchInsert(List<PenaltyRecordPO> records);

    PenaltyActionPO selectByPrimaryKey(Long id);

    List<PenaltyRecordPO> selectExample(PenaltyRecordPO penaltyRecord);

    boolean updateByPrimaryKey(PenaltyRecordPO record);

}
