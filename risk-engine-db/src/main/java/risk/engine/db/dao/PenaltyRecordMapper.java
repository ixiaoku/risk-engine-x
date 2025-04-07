package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.PenaltyRecordPO;

import java.util.List;

public interface PenaltyRecordMapper {

    int batchInsert(@Param("list") List<PenaltyRecordPO> records);

    List<PenaltyRecordPO> selectExample(PenaltyRecordPO penaltyRecord);

    int updateByPrimaryKey(PenaltyRecordPO record);
}