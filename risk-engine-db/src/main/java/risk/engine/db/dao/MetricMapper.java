package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.Metric;

import java.util.List;

public interface MetricMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByIncidentCode(String incidentCode);

    int insert(Metric record);

    Metric selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Metric record);

    List<Metric> selectByExample(Metric record);

    int batchInsert(@Param("list") List<Metric> record);

}