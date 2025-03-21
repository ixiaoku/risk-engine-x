package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import risk.engine.db.dao.IncidentMapper;
import risk.engine.db.dao.IndicatorMapper;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Indicator;
import risk.engine.dto.dto.rule.IndicatorDTO;
import risk.engine.dto.enums.IndicatorTypeEnum;
import risk.engine.dto.enums.IndictorSourceEnum;
import risk.engine.dto.param.IncidentParam;
import risk.engine.dto.result.IncidentResult;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByPrimaryKey(Long id) {
        Incident incident = incidentMapper.selectByPrimaryKey(id);
        indicatorMapper.deleteByIncidentCode(incident.getIncidentCode());
        return incidentMapper.deleteByPrimaryKey(id) > 0 && indicatorMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
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
        incident.setRequestPayload(incidentParam.getRequestPayload());
        List<Indicator> indicatorList = getIndicatorList(incidentParam);
        //保存事件 指标
        return incidentMapper.insert(incident) > 0 && indicatorMapper.batchInsert(indicatorList) > 0;
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
    public IncidentResult selectByPrimaryKey(Long id) {
        Incident incident = incidentMapper.selectByPrimaryKey(id);
        if (incident == null) {
            return null;
        }
        IncidentResult incidentResult = new IncidentResult();
        BeanUtils.copyProperties(incident, incidentResult);
        List<IndicatorDTO> indicators = JSON.parseArray(incident.getRequestPayload(), IndicatorDTO.class);
        incidentResult.setIndicators(indicators);
        return incidentResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByPrimaryKey(IncidentParam param) {
        Incident incident = new Incident();
        BeanUtils.copyProperties(param, incident);
        incident.setUpdateTime(LocalDateTime.now());
        incident.setRequestPayload(new Gson().toJson(param.getRequestPayload()));
        Boolean flag1 = incidentMapper.updateByPrimaryKey(incident) > 0;
        Boolean flag2 = indicatorMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
        Boolean flag3 = indicatorMapper.batchInsert(null) > 0;
        return flag1 && flag2 && flag3;
    }

    @Override
    public List<IncidentResult> list(IncidentParam incidentParam) {
        Incident incident = new Incident();
        incident.setIncidentCode(incidentParam.getIncidentCode());
        incident.setIncidentName(incidentParam.getIncidentName());
        incident.setStatus(incidentParam.getStatus());
        List<Incident> incidentList = incidentMapper.selectByExample(incident);
        if (CollectionUtils.isEmpty(incidentList)) {
            return List.of();
        }
        return incidentList.stream().map(i -> {
            IncidentResult incidentResult = new IncidentResult();
            BeanUtils.copyProperties(i, incidentResult);
            List<IndicatorDTO> indicators = JSON.parseArray(i.getRequestPayload(), IndicatorDTO.class);
            incidentResult.setIndicators(indicators);
            return incidentResult;
        }).collect(Collectors.toList());
    }

    /**
     * 获取指标list
     * @param incidentParam 事件
     * @return 结果
     */
    private List<Indicator> getIndicatorList(IncidentParam incidentParam) {
        JSONObject jsonObject = JSON.parseObject(incidentParam.getRequestPayload());
        return jsonObject.entrySet().stream()
                .map(key -> {
                    Indicator indicator = new Indicator();
                    indicator.setIncidentCode(incidentParam.getIncidentCode());
                    indicator.setIndicatorCode(key.getKey());
                    indicator.setIndicatorName("名字");
                    indicator.setIndicatorValue(key.getValue().toString());
                    indicator.setIndicatorDesc("备注");
                    indicator.setIndicatorSource(IndictorSourceEnum.ATTRIBUTE.getCode());
                    indicator.setIndicatorType(IndicatorTypeEnum.STRING.getCode());
                    indicator.setOperator(incidentParam.getOperator());
                    indicator.setCreateTime(LocalDateTime.now());
                    indicator.setUpdateTime(LocalDateTime.now());
                    return indicator;
                }).collect(Collectors.toList());
    }
}
