package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.PenaltyActionPO;
import risk.engine.db.entity.PenaltyRecordPO;

import java.util.List;

public interface PenaltyRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PenaltyRecordPO record);

    int batchInsert(@Param("list") List<PenaltyRecordPO> records);

    PenaltyActionPO selectByPrimaryKey(Long id);

    List<PenaltyRecordPO> selectExample(PenaltyRecordPO penaltyRecord);

    int updateByPrimaryKey(PenaltyRecordPO record);
}