package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.MetricPO;

import java.util.List;

public interface MetricMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByIncidentCode(String incidentCode);

    int insert(MetricPO record);

    MetricPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(MetricPO record);

    List<MetricPO> selectByExample(MetricPO record);

    int batchInsert(@Param("list") List<MetricPO> record);

}