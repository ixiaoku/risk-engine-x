package risk.engine.db.dao;

import risk.engine.db.entity.Indicator;

import java.util.List;

public interface IndicatorMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Indicator record);

    Indicator selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Indicator record);

    List<Indicator> selectByExample(Indicator record);
}