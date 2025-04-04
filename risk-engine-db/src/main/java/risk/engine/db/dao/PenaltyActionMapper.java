package risk.engine.db.dao;

import risk.engine.db.entity.PenaltyActionPO;

import java.util.List;

public interface PenaltyActionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PenaltyActionPO record);

    PenaltyActionPO selectByPrimaryKey(Long id);

    List<PenaltyActionPO> selectByExample(PenaltyActionPO penalty);

    int updateByPrimaryKey(PenaltyActionPO record);
}