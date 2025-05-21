package risk.engine.service.service;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.MetricPO;
import risk.engine.dto.dto.rule.MetricDTO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:55
 * @Version: 1.0
 */
public interface IMetricService {

    List<MetricPO> selectByExample(MetricPO record);

    boolean batchInsert(@Param("list") List<MetricPO> record);

    List<MetricDTO> selectByIncidentCode(String incidentCode);

}
