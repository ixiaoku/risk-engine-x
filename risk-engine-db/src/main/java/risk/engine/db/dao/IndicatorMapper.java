package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.Indicator;

import java.util.List;

public interface IndicatorMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByIncidentCode(String incidentCode);

    int insert(Indicator record);

    Indicator selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Indicator record);

    List<Indicator> selectByExample(Indicator record);

    int batchInsert(@Param("list") List<Indicator> record);

}