package risk.engine.service.service;

import risk.engine.db.entity.IncidentPO;
import risk.engine.dto.dto.rule.MetricDTO;
import risk.engine.dto.param.IncidentParam;
import risk.engine.dto.vo.IncidentVO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
public interface IIncidentService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(IncidentParam incidentParam);

    List<IncidentPO> selectByExample(IncidentPO incident);

    IncidentPO selectByIncidentCode(String incidentCode);

    IncidentVO selectByPrimaryKey(Long id);

    List<MetricDTO> parseMetric(String incidentCode, String requestPayload);

    boolean updateByPrimaryKey(IncidentParam incidentParam);

    List<IncidentVO> list(IncidentParam incidentParam);
}
