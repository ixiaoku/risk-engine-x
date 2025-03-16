package risk.engine.service.service.impl;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import risk.engine.db.dao.IncidentMapper;
import risk.engine.db.dao.IndicatorMapper;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Indicator;
import risk.engine.dto.enums.IndictorSourceEnum;
import risk.engine.dto.param.IncidentParam;
import risk.engine.service.service.IIncidentService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
@Service
public class IIncidentServiceImpl implements IIncidentService {

    @Resource
    private IncidentMapper incidentMapper;

    @Resource
    private IndicatorMapper indicatorMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return incidentMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(IncidentParam incidentParam) {
        Incident incident = new Incident();
        incident.setIncidentCode(incidentParam.getIncidentCode());
        incident.setIncidentName(incidentParam.getIncidentName());
        incident.setDecisionResult(incidentParam.getDecisionResult());
        incident.setStatus(incidentParam.getStatus());
        incident.setResponsiblePerson(incidentParam.getResponsiblePerson());
        incident.setOperator(incidentParam.getOperator());
        incident.setCreateTime(LocalDateTime.now());
        incident.setUpdateTime(LocalDateTime.now());
        incident.setRequestPayload(new Gson().toJson(incidentParam.getIndicators()));

        //保存指标
        List<Indicator> indicatorList = incidentParam.getIndicators().stream()
                .map(indicatorDTO -> {
                    Indicator indicator = new Indicator();
                    indicator.setIncidentCode(indicatorDTO.getIndicatorCode());
                    indicator.setIndicatorName(indicatorDTO.getIndicatorName());
                    indicator.setIndicatorValue(indicatorDTO.getIndicatorValue());
                    indicator.setIndicatorDesc(indicatorDTO.getIndicatorDesc());
                    indicator.setIndicatorSource(IndictorSourceEnum.ATTRIBUTE.getCode());
                    indicator.setIndicatorType(indicatorDTO.getIndicatorType());
                    indicator.setOperator(incidentParam.getOperator());
                    indicator.setCreateTime(LocalDateTime.now());
                    indicator.setUpdateTime(LocalDateTime.now());
                    return indicator;
                }).collect(Collectors.toList());
        indicatorList.forEach(indicator -> indicatorMapper.insert(indicator));
        //保存事件
        return incidentMapper.insert(incident) > 0;
    }

    @Override
    public List<Incident> selectByExample(Incident incident) {
        return incidentMapper.selectByExample(incident);
    }

    @Override
    public Incident selectByIncidentCode(String incidentCode) {
        return incidentMapper.selectByIncidentCode(incidentCode);
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
