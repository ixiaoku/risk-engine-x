package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import risk.engine.common.util.DateTimeUtil;
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
import java.math.BigDecimal;
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
        return incidentMapper.deleteByPrimaryKey(id) > 0 && indicatorMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(IncidentParam incidentParam) {
        Incident incidentQuery = new Incident();
        incidentQuery.setIncidentCode(incidentParam.getIncidentCode());
        List<Incident> incidentList = incidentMapper.selectByExample(incidentQuery);
        if (CollectionUtils.isNotEmpty(incidentList)) {
            throw new RuntimeException("事件标识已存在");
        }
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
    public List<IndicatorDTO> parseIndicator(String incidentCode, String requestPayload) {

        Indicator indicatorQuery = new Indicator();
        indicatorQuery.setIncidentCode(incidentCode);
        List<Indicator> indicatorList = indicatorMapper.selectByExample(indicatorQuery);
        //新增
        if (CollectionUtils.isEmpty(indicatorList)) {
            JSONObject jsonObject = JSON.parseObject(requestPayload);
            return jsonObject.entrySet().stream()
                    .map(key -> {
                        IndicatorDTO indicator = new IndicatorDTO();
                        indicator.setIndicatorCode(key.getKey());
                        indicator.setIndicatorName("指标名字");
                        indicator.setIndicatorValue(key.getValue().toString());
                        indicator.setIndicatorDesc("指标备注");
                        if (key.getValue() instanceof Integer) {
                            indicator.setIndicatorType(IndicatorTypeEnum.INTEGER.getCode());
                        }  else if (key.getValue() instanceof BigDecimal) {
                            indicator.setIndicatorType(IndicatorTypeEnum.BIG_DECIMAL.getCode());
                        } else if (key.getValue() instanceof Boolean) {
                            indicator.setIndicatorType(IndicatorTypeEnum.BOOLEAN.getCode());
                        } else if (JSON.isValidArray(key.getValue().toString())) {
                            indicator.setIndicatorType(IndicatorTypeEnum.JSON_ARRAY.getCode());
                        } else if (JSON.isValidObject(key.getValue().toString())) {
                            indicator.setIndicatorType(IndicatorTypeEnum.JSON_OBJECT.getCode());
                        }else {
                            indicator.setIndicatorType(IndicatorTypeEnum.STRING.getCode());
                        }
                        return indicator;
                    }).collect(Collectors.toList());
        }
        //编辑
        return indicatorList.stream().map(indicator -> {
            IndicatorDTO indicatorDTO = new IndicatorDTO();
            indicatorDTO.setIndicatorCode(indicator.getIndicatorCode());
            indicatorDTO.setIndicatorName(indicator.getIndicatorName());
            indicatorDTO.setIndicatorType(indicator.getIndicatorType());
            indicatorDTO.setIndicatorDesc(indicator.getIndicatorDesc());
            indicatorDTO.setIndicatorValue(indicator.getIndicatorValue());
            return indicatorDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByPrimaryKey(IncidentParam param) {
        Incident incident = new Incident();
        BeanUtils.copyProperties(param, incident);
        incident.setUpdateTime(LocalDateTime.now());
        incident.setRequestPayload(param.getRequestPayload());
        Boolean flag1 = incidentMapper.updateByPrimaryKey(incident) > 0;
        Boolean flag2 = indicatorMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
        List<Indicator> indicatorList = getIndicatorList(param);
        Boolean flag3 = indicatorMapper.batchInsert(indicatorList) > 0;
        return flag1 && flag2 && flag3;
    }

    @Override
    public List<IncidentResult> list(IncidentParam incidentParam) {
        Incident incidentQuery = new Incident();
        incidentQuery.setIncidentCode(incidentParam.getIncidentCode());
        incidentQuery.setIncidentName(incidentParam.getIncidentName());
        incidentQuery.setStatus(incidentParam.getStatus());
        List<Incident> incidentList = incidentMapper.selectByExample(incidentQuery);
        if (CollectionUtils.isEmpty(incidentList)) {
            return List.of();
        }
        return incidentList.stream().map(incident -> {
            IncidentResult incidentResult = new IncidentResult();
            incidentResult.setIncidentCode(incident.getIncidentCode());
            incidentResult.setIncidentName(incident.getIncidentName());
            incidentResult.setStatus(incident.getStatus());
            incidentResult.setOperator(incident.getOperator());
            incidentResult.setCreateTime(DateTimeUtil.getTimeByLocalDateTime(incident.getCreateTime()));
            incidentResult.setUpdateTime(DateTimeUtil.getTimeByLocalDateTime(incident.getUpdateTime()));
            return incidentResult;
        }).collect(Collectors.toList());
    }

    /**
     * 获取指标list
     * @param incidentParam 事件
     * @return 结果
     */
    private List<Indicator> getIndicatorList(IncidentParam incidentParam) {
        return incidentParam.getIndicators().stream()
                .map(indicatorDTO -> {
                    Indicator indicator = new Indicator();
                    indicator.setIncidentCode(incidentParam.getIncidentCode());
                    indicator.setIndicatorCode(indicatorDTO.getIndicatorCode());
                    indicator.setIndicatorName(indicatorDTO.getIndicatorName());
                    indicator.setIndicatorValue(indicatorDTO.getIndicatorValue());
                    indicator.setIndicatorDesc("备注");
                    indicator.setIndicatorSource(IndictorSourceEnum.ATTRIBUTE.getCode());
                    indicator.setIndicatorType(indicatorDTO.getIndicatorType());
                    indicator.setOperator(incidentParam.getOperator());
                    indicator.setCreateTime(LocalDateTime.now());
                    indicator.setUpdateTime(LocalDateTime.now());
                    return indicator;
                }).collect(Collectors.toList());

    }
}
