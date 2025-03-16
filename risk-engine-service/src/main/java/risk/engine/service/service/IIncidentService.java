package risk.engine.service.service;

import risk.engine.db.entity.Incident;
import risk.engine.dto.param.IncidentParam;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
public interface IIncidentService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(IncidentParam incidentParam);

    List<Incident> selectByExample(Incident incident);

    Incident selectByIncidentCode(String incidentCode);

    Incident selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(Incident record);

}
