package risk.engine.service.common.cache;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import risk.engine.common.redis.RedisUtil;
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.MetricPO;
import risk.engine.dto.dto.IncidentDTO;
import risk.engine.dto.dto.rule.MetricDTO;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IMetricService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/4/5 17:46
 * @Version: 1.0
 */
@Component
public class RedisIncidentMetricCache implements ApplicationRunner {
    
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private IMetricService metricService;

    @Resource
    private IIncidentService incidentService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, List<MetricDTO>> metricPOList = selectMetricPOList();
        Map<String, IncidentDTO> incidentDTOMap = selectIncidentList();
        redisUtil.set("metricPOList", metricPOList, 3600);
        redisUtil.set("incidentDTOMap", incidentDTOMap, 3600);
    }

    /**
     * 查库缓存指标
     * @return 结果
     */
    private Map<String, List<MetricDTO>> selectMetricPOList() {
        List<MetricPO> metricPOList = metricService.selectByExample(new MetricPO());
        if (CollectionUtils.isEmpty(metricPOList)) {
            return Map.of();
        }
        return (ConcurrentMap<String, List<MetricDTO>>) metricPOList.stream()
                .map(metric -> {
                    MetricDTO metricDTO = new MetricDTO();
                    metricDTO.setIncidentCode(metric.getIncidentCode());
                    metricDTO.setMetricCode(metric.getMetricCode());
                    metricDTO.setMetricName(metric.getMetricName());
                    metricDTO.setMetricType(metric.getMetricType());
                    return metricDTO;
                }).collect(Collectors.groupingBy(MetricDTO::getIncidentCode));
    }

    /**
     * 查库缓存事件
     * @return 结果
     */
    private Map<String, IncidentDTO> selectIncidentList() {
        IncidentPO incident = new IncidentPO();
        incident.setStatus(IncidentStatusEnum.ONLINE.getCode());
        List<IncidentPO> incidentList = incidentService.selectByExample(incident);
        if (CollectionUtils.isEmpty(incidentList)) {
            return Map.of();
        }
        return incidentList.stream().map(i -> {
            IncidentDTO incidentDTO = new IncidentDTO();
            incidentDTO.setIncidentCode(i.getIncidentCode());
            incidentDTO.setIncidentName(i.getIncidentName());
            incidentDTO.setDecisionResult(i.getDecisionResult());
            incidentDTO.setRequestPayload(i.getRequestPayload());
            return incidentDTO;
        }).collect(Collectors.toMap(IncidentDTO::getIncidentCode, Function.identity()));
    }
    
}
