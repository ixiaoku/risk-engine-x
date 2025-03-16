package risk.engine.db.dao;

import risk.engine.db.entity.Penalty;
import risk.engine.db.entity.PenaltyRecord;

public interface PenaltyRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PenaltyRecord record);

    Penalty selectByPrimaryKey(Long id);

    int updateByPrimaryKey(PenaltyRecord record);
}