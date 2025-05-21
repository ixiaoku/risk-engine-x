package risk.engine.service.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.db.dao.MetricMapper;
import risk.engine.db.entity.CounterMetricPO;
import risk.engine.db.entity.MetricPO;
import risk.engine.dto.dto.rule.MetricDTO;
import risk.engine.dto.enums.CounterStatusEnum;
import risk.engine.dto.enums.MetricSourceEnum;
import risk.engine.service.service.ICounterMetricService;
import risk.engine.service.service.IMetricService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/16 12:55
 * @Version: 1.0
 */
@Service
public class MetricServiceImpl implements IMetricService {

    @Resource
    private MetricMapper metricMapper;

    @Resource
    private ICounterMetricService counterMetricService;

    @Override
    public List<MetricPO> selectByExample(MetricPO record) {
        return metricMapper.selectByExample(record);
    }

    @Override
    public boolean batchInsert(List<MetricPO> record) {
        return metricMapper.batchInsert(record) > 0;
    }

    @Override
    public List<MetricDTO> selectByIncidentCode(String incidentCode) {
        List<MetricDTO> metricDTOList = new ArrayList<>();
        //事件属性特征 业务方传入
        MetricPO metricQuery = new MetricPO();
        metricQuery.setIncidentCode(incidentCode);
        List<MetricPO> metricList = metricMapper.selectByExample(metricQuery);
        if (CollectionUtils.isNotEmpty(metricList)) {
            List<MetricDTO> metricDTOList1  = metricList.stream().map(metricPO -> {
                MetricDTO metricDTO = new MetricDTO();
                metricDTO.setIncidentCode(metricPO.getIncidentCode());
                metricDTO.setMetricCode(metricPO.getMetricCode());
                metricDTO.setMetricName(metricPO.getMetricName());
                metricDTO.setMetricType(metricPO.getMetricType());
                metricDTO.setMetricSource(metricPO.getMetricSource());
                return metricDTO;
            }).collect(Collectors.toList());
            metricDTOList.addAll(metricDTOList1);
        }
        //计数器指标
        CounterMetricPO counterQuery = new CounterMetricPO();
        counterQuery.setIncidentCode(incidentCode);
        counterQuery.setStatus(CounterStatusEnum.ONLINE.getCode());
        List<CounterMetricPO> counterMetricPOS = counterMetricService.selectExample(counterQuery);
        if (CollectionUtils.isNotEmpty(counterMetricPOS)) {
            List<MetricDTO> metricDTOList2 = counterMetricPOS.stream().map(metricPO -> {
                MetricDTO metricDTO = new MetricDTO();
                metricDTO.setIncidentCode(metricPO.getIncidentCode());
                metricDTO.setMetricCode(metricPO.getMetricCode());
                metricDTO.setMetricName(metricPO.getMetricName());
                metricDTO.setMetricType(metricPO.getMetricType());
                metricDTO.setMetricSource(MetricSourceEnum.COUNT.getCode());
                return metricDTO;
            }).collect(Collectors.toList());
            metricDTOList.addAll(metricDTOList2);
        }
        if(CollectionUtils.isEmpty(metricDTOList)) {
            return List.of();
        }
        return metricDTOList;
    }
}
