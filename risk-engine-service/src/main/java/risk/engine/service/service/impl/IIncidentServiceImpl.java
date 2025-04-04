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
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.MetricPO;
import risk.engine.dto.dto.rule.MetricDTO;
import risk.engine.dto.enums.MetricSourceEnum;
import risk.engine.dto.enums.MetricTypeEnum;
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
        IncidentPO incidentQuery = new IncidentPO();
        incidentQuery.setIncidentCode(incidentParam.getIncidentCode());
        List<IncidentPO> incidentList = incidentMapper.selectByExample(incidentQuery);
        if (CollectionUtils.isNotEmpty(incidentList)) {
            throw new RuntimeException("事件标识已存在");
        }
        IncidentPO incident = new IncidentPO();
        incident.setIncidentCode(incidentParam.getIncidentCode());
        incident.setIncidentName(incidentParam.getIncidentName());
        incident.setDecisionResult(incidentParam.getDecisionResult());
        incident.setStatus(incidentParam.getStatus());
        incident.setResponsiblePerson(incidentParam.getResponsiblePerson());
        incident.setOperator(incidentParam.getOperator());
        incident.setCreateTime(LocalDateTime.now());
        incident.setUpdateTime(LocalDateTime.now());
        incident.setRequestPayload(incidentParam.getRequestPayload());
        List<MetricPO> metricList = getMetricList(incidentParam);
        //保存事件 指标
        return incidentMapper.insert(incident) > 0 && metricMapper.batchInsert(metricList) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByPrimaryKey(Long id) {
        IncidentPO incident = incidentMapper.selectByPrimaryKey(id);
        return incidentMapper.deleteByPrimaryKey(id) > 0 && metricMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByPrimaryKey(IncidentParam param) {
        IncidentPO incident = new IncidentPO();
        BeanUtils.copyProperties(param, incident);
        incident.setUpdateTime(LocalDateTime.now());
        incident.setRequestPayload(param.getRequestPayload());
        boolean flag1 = incidentMapper.updateByPrimaryKey(incident) > 0;
        boolean flag2 = metricMapper.deleteByIncidentCode(incident.getIncidentCode()) > 0;
        List<MetricPO> metricList = getMetricList(param);
        boolean flag3 = metricMapper.batchInsert(metricList) > 0;
        return flag1 && flag2 && flag3;
    }

    @Override
    public List<IncidentPO> selectByExample(IncidentPO incident) {
        return incidentMapper.selectByExample(incident);
    }

    @Override
    public IncidentPO selectByIncidentCode(String incidentCode) {
        return incidentMapper.selectByIncidentCode(incidentCode);
    }

    @Override
    public IncidentVO selectByPrimaryKey(Long id) {
        IncidentPO incident = incidentMapper.selectByPrimaryKey(id);
        if (incident == null) {
            return null;
        }
        IncidentVO incidentVO = getIncidentResult(incident);
        List<MetricDTO> metrics = JSON.parseArray(incident.getRequestPayload(), MetricDTO.class);
        incidentVO.setMetrics(metrics);
        return incidentVO;
    }

    @Override
    public List<MetricDTO> parseMetric(String incidentCode, String requestPayload) {
        MetricPO metricQuery = new MetricPO();
        metricQuery.setIncidentCode(incidentCode);
        List<MetricPO> metricList = metricMapper.selectByExample(metricQuery);
        //@todo 待优化如果是增加或者减少字段 或者改变类型 我还需要兼容
        //新增
        JSONObject jsonObject = JSON.parseObject(requestPayload);
        if (CollectionUtils.isEmpty(metricList)) {
            return jsonObject.entrySet().stream()
                    .map(key -> {
                        MetricDTO metric = new MetricDTO();
                        metric.setMetricCode(key.getKey());
                        metric.setSampleValue(key.getValue().toString());
                        metric.setMetricName("指标名字");
                        metric.setMetricDesc("备注");
                        if (key.getValue() instanceof Integer) {
                            metric.setMetricType(MetricTypeEnum.INTEGER.getCode());
                        }  else if (key.getValue() instanceof BigDecimal) {
                            metric.setMetricType(MetricTypeEnum.BIG_DECIMAL.getCode());
                        } else if (key.getValue() instanceof Boolean) {
                            metric.setMetricType(MetricTypeEnum.BOOLEAN.getCode());
                        } else if (JSON.isValidArray(key.getValue().toString())) {
                            metric.setMetricType(MetricTypeEnum.JSON_ARRAY.getCode());
                        } else if (JSON.isValidObject(key.getValue().toString())) {
                            metric.setMetricType(MetricTypeEnum.JSON_OBJECT.getCode());
                        }else {
                            metric.setMetricType(MetricTypeEnum.STRING.getCode());
                        }
                        return metric;
                    }).collect(Collectors.toList());
        }
        //编辑
        return metricList.stream().map(metric -> {
            MetricDTO metricDTO = new MetricDTO();
            metricDTO.setMetricCode(metric.getMetricCode());
            metricDTO.setMetricName(metric.getMetricName());
            metricDTO.setMetricType(metric.getMetricType());
            metricDTO.setMetricDesc(metric.getMetricDesc());
            metricDTO.setSampleValue(metric.getSampleValue());
            return metricDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<IncidentVO> list(IncidentParam incidentParam) {
        IncidentPO incidentQuery = new IncidentPO();
        incidentQuery.setIncidentCode(incidentParam.getIncidentCode());
        incidentQuery.setIncidentName(incidentParam.getIncidentName());
        incidentQuery.setStatus(incidentParam.getStatus());
        List<IncidentPO> incidentList = incidentMapper.selectByExample(incidentQuery);
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
    private IncidentVO getIncidentResult(IncidentPO incident) {
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
    private List<MetricPO> getMetricList(IncidentParam incidentParam) {
        return incidentParam.getMetrics().stream()
                .map(metricDTO -> {
                    MetricPO metric = new MetricPO();
                    metric.setIncidentCode(incidentParam.getIncidentCode());
                    metric.setMetricCode(metricDTO.getMetricCode());
                    metric.setMetricName(metricDTO.getMetricName());
                    metric.setSampleValue(metricDTO.getSampleValue());
                    metric.setMetricDesc(metricDTO.getMetricDesc());
                    metric.setMetricSource(MetricSourceEnum.ATTRIBUTE.getCode());
                    metric.setMetricType(metricDTO.getMetricType());
                    metric.setOperator(incidentParam.getOperator());
                    metric.setCreateTime(LocalDateTime.now());
                    metric.setUpdateTime(LocalDateTime.now());
                    return metric;
                }).collect(Collectors.toList());

    }
}
