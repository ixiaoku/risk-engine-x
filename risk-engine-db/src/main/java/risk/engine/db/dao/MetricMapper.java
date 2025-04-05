package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.MetricPO;

import java.util.List;

public interface MetricMapper {


    int deleteByIncidentCode(String incidentCode);

    List<MetricPO> selectByExample(MetricPO record);

    int batchInsert(@Param("list") List<MetricPO> record);

}