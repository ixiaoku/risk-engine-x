package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.Penalty;
import risk.engine.db.entity.PenaltyRecord;

import java.util.List;

public interface PenaltyRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PenaltyRecord record);

    int batchInsert(@Param("list") List<PenaltyRecord> records);

    Penalty selectByPrimaryKey(Long id);

    List<PenaltyRecord> selectExample(PenaltyRecord penaltyRecord);

    int updateByPrimaryKey(PenaltyRecord record);
}