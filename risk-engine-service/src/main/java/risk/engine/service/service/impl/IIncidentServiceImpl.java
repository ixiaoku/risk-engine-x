package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.dao.IncidentMapper;
import risk.engine.db.dao.MetricMapper;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Metric;
import risk.engine.dto.dto.rule.MetricDTO;
import risk.engine.dto.enums.MetricTypeEnum;
import risk.engine.dto.enums.MetricSourceEnum;
import risk.engine.dto.param.IncidentParam;
import risk.engine.dto.vo.IncidentVO;
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
    private MetricMapper metricMapper;

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
        List<Metric> metricList = getIndicatorList(incidentParam);
        //保存事件 指标
        return incidentMapper.insert(incident) > 0 && metricMapper.batchInsert(metricList) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByPrimaryKey(Long id) {
        Incident incident = incidentMapper.selectByPrimaryKey(id);
        return incidentMapper.deleteByPrimaryKey(id) > 0 && metricMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByPrimaryKey(IncidentParam param) {
        Incident incident = new Incident();
        BeanUtils.copyProperties(param, incident);
        incident.setUpdateTime(LocalDateTime.now());
        incident.setRequestPayload(param.getRequestPayload());
        Boolean flag1 = incidentMapper.updateByPrimaryKey(incident) > 0;
        Boolean flag2 = metricMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
        List<Metric> metricList = getIndicatorList(param);
        Boolean flag3 = metricMapper.batchInsert(metricList) > 0;
        return flag1 && flag2 && flag3;
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
    public IncidentVO selectByPrimaryKey(Long id) {
        Incident incident = incidentMapper.selectByPrimaryKey(id);
        if (incident == null) {
            return null;
        }
        IncidentVO incidentVO = getIncidentResult(incident);
        List<MetricDTO> indicators = JSON.parseArray(incident.getRequestPayload(), MetricDTO.class);
        incidentVO.setIndicators(indicators);
        return incidentVO;
    }

    @Override
    public List<MetricDTO> parseIndicator(String incidentCode, String requestPayload) {
        Metric metricQuery = new Metric();
        metricQuery.setIncidentCode(incidentCode);
        List<Metric> metricList = metricMapper.selectByExample(metricQuery);
        //新增
        if (CollectionUtils.isEmpty(metricList)) {
            JSONObject jsonObject = JSON.parseObject(requestPayload);
            return jsonObject.entrySet().stream()
                    .map(key -> {
                        MetricDTO indicator = new MetricDTO();
                        indicator.setMetricCode(key.getKey());
                        indicator.setMetricName("指标名字");
                        indicator.setMetricValue(key.getValue().toString());
                        indicator.setMetricDesc("备注");
                        if (key.getValue() instanceof Integer) {
                            indicator.setMetricType(MetricTypeEnum.INTEGER.getCode());
                        }  else if (key.getValue() instanceof BigDecimal) {
                            indicator.setMetricType(MetricTypeEnum.BIG_DECIMAL.getCode());
                        } else if (key.getValue() instanceof Boolean) {
                            indicator.setMetricType(MetricTypeEnum.BOOLEAN.getCode());
                        } else if (JSON.isValidArray(key.getValue().toString())) {
                            indicator.setMetricType(MetricTypeEnum.JSON_ARRAY.getCode());
                        } else if (JSON.isValidObject(key.getValue().toString())) {
                            indicator.setMetricType(MetricTypeEnum.JSON_OBJECT.getCode());
                        }else {
                            indicator.setMetricType(MetricTypeEnum.STRING.getCode());
                        }
                        return indicator;
                    }).collect(Collectors.toList());
        }
        //编辑
        return metricList.stream().map(indicator -> {
            MetricDTO metricDTO = new MetricDTO();
            metricDTO.setMetricCode(indicator.getMetricCode());
            metricDTO.setMetricName(indicator.getMetricName());
            metricDTO.setMetricType(indicator.getMetricType());
            metricDTO.setMetricDesc(indicator.getMetricDesc());
            metricDTO.setMetricValue(indicator.getSampleValue());
            return metricDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<IncidentVO> list(IncidentParam incidentParam) {
        Incident incidentQuery = new Incident();
        incidentQuery.setIncidentCode(incidentParam.getIncidentCode());
        incidentQuery.setIncidentName(incidentParam.getIncidentName());
        incidentQuery.setStatus(incidentParam.getStatus());
        List<Incident> incidentList = incidentMapper.selectByExample(incidentQuery);
        if (CollectionUtils.isEmpty(incidentList)) {
            return List.of();
        }
        return incidentList.stream().map(this::getIncidentResult).collect(Collectors.toList());
    }

    /**
     * 转换类
     * @param incident 实体类
     * @return 结果
     */
    private IncidentVO getIncidentResult(Incident incident) {
        IncidentVO incidentVO = new IncidentVO();
        incidentVO.setId(incident.getId());
        incidentVO.setIncidentCode(incident.getIncidentCode());
        incidentVO.setIncidentName(incident.getIncidentName());
        incidentVO.setRequestPayload(incident.getRequestPayload());
        incidentVO.setStatus(incident.getStatus());
        incidentVO.setDecisionResult(incident.getDecisionResult());
        incidentVO.setResponsiblePerson(incident.getResponsiblePerson());
        incidentVO.setOperator(incident.getOperator());
        incidentVO.setCreateTime(DateTimeUtil.getTimeByLocalDateTime(incident.getCreateTime()));
        incidentVO.setUpdateTime(DateTimeUtil.getTimeByLocalDateTime(incident.getUpdateTime()));
        return incidentVO;
    }

    /**
     * 获取指标list
     * @param incidentParam 事件
     * @return 结果
     */
    private List<Metric> getIndicatorList(IncidentParam incidentParam) {
        return incidentParam.getIndicators().stream()
                .map(metricDTO -> {
                    Metric metric = new Metric();
                    metric.setIncidentCode(incidentParam.getIncidentCode());
                    metric.setMetricCode(metricDTO.getMetricCode());
                    metric.setMetricName(metricDTO.getMetricName());
                    metric.setSampleValue(metricDTO.getMetricValue());
                    metric.setMetricDesc("备注");
                    metric.setMetricSource(MetricSourceEnum.ATTRIBUTE.getCode());
                    metric.setMetricType(metricDTO.getMetricType());
                    metric.setOperator(incidentParam.getOperator());
                    metric.setCreateTime(LocalDateTime.now());
                    metric.setUpdateTime(LocalDateTime.now());
                    return metric;
                }).collect(Collectors.toList());

    }
}
