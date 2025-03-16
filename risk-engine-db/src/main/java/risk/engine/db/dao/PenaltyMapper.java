package risk.engine.db.dao;

import risk.engine.db.entity.Penalty;

import java.util.List;

public interface PenaltyMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Penalty record);

    Penalty selectByPrimaryKey(Long id);

    List<Penalty> selectByExample(Penalty penalty);

    int updateByPrimaryKey(Penalty record);
}