package risk.engine.db.dao;

import risk.engine.db.entity.Penalty;
import risk.engine.db.entity.PenaltyRecord;

import java.util.List;

public interface PenaltyRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PenaltyRecord record);

    Penalty selectByPrimaryKey(Long id);

    List<PenaltyRecord> selectExample(PenaltyRecord penaltyRecord);

    int updateByPrimaryKey(PenaltyRecord record);
}