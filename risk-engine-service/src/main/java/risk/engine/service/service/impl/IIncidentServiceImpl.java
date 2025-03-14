package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.IncidentMapper;
import risk.engine.db.entity.Incident;
import risk.engine.service.service.IIncidentService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
@Service
public class IIncidentServiceImpl implements IIncidentService {

    @Resource
    private IncidentMapper incidentMapper;


    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return incidentMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(Incident record) {
        return incidentMapper.insert(record) > 0;
    }

    @Override
    public List<Incident> selectByExample(Incident incident) {
        return incidentMapper.selectByExample(incident);
    }

    @Override
    public Incident selectByPrimaryKey(Long id) {
        return incidentMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(Incident record) {
        return incidentMapper.updateByPrimaryKey(record) > 0;
    }
}
