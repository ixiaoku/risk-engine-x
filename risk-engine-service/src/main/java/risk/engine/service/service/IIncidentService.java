package risk.engine.service.service;

import risk.engine.db.entity.Incident;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
public interface IIncidentService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(Incident record);

    List<Incident> selectByExample(Incident incident);

    Incident selectByIncidentCode(String incidentCode);

    Incident selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(Incident record);

}
