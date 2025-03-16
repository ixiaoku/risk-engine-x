package risk.engine.db.dao;

import risk.engine.db.entity.Penalty;

public interface PenaltyMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Penalty record);

    Penalty selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Penalty record);
}