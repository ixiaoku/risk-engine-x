package risk.engine.service.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.db.dao.MetricMapper;
import risk.engine.db.entity.MetricPO;
import risk.engine.dto.dto.rule.MetricDTO;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 指标缓存
 * @Author: X
 * @Date: 2025/3/20 17:16
 * @Version: 1.0
 */

@Slf4j
@Component
public class GuavaMetricCache {

    @Resource
    private MetricMapper metricMapper;

    private final Cache<String, List<MetricDTO>> metricCache = CacheBuilder.newBuilder()
            .maximumSize(256)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    @PostConstruct
    private void loadCache() {
        ConcurrentHashMap<String, List<MetricDTO>> metricMap = selectMetricPOList();
        metricCache.putAll(metricMap);
    }

    /**
     * 获取缓存 如果为空主动查库
     * @param incidentCode 事件code
     * @return 结果
     */
    public List<MetricDTO> getCache(String incidentCode) {
        List<MetricDTO> metricDTOList = metricCache.getIfPresent(incidentCode);
        if (CollectionUtils.isNotEmpty(metricDTOList)) {
            return metricDTOList;
        }
        ConcurrentHashMap<String, List<MetricDTO>> map = selectMetricPOList();
        return map.get(incidentCode);
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        CompletableFuture.runAsync(() -> {
            ConcurrentHashMap<String, List<MetricDTO>> newData = selectMetricPOList();
            metricCache.invalidateAll();
            metricCache.putAll(newData);
            log.info("Metric缓存刷新完毕，条数：{}", newData.size());
        }).exceptionally(ex -> {
            log.error("刷新缓存 异步任务失败, 异常: {}", ex.getMessage(), ex);
            return null;
        });
    }

    /**
     * 查库缓存指标
     * @return 结果
     */
    private ConcurrentHashMap<String, List<MetricDTO>> selectMetricPOList() {
        ConcurrentHashMap<String, List<MetricDTO>> data = new ConcurrentHashMap<>();
        List<MetricPO> metricPOList = metricMapper.selectByExample(new MetricPO());
        if (CollectionUtils.isEmpty(metricPOList)) {
            return data;
        }
        return (ConcurrentHashMap<String, List<MetricDTO>>) metricPOList.stream().map(metric -> {
            MetricDTO metricDTO = new MetricDTO();
            metricDTO.setIncidentCode(metric.getIncidentCode());
            metricDTO.setMetricCode(metric.getMetricCode());
            metricDTO.setMetricName(metric.getMetricName());
            metricDTO.setMetricType(metric.getMetricType());
            return metricDTO;
        }).collect(Collectors.groupingByConcurrent(MetricDTO::getIncidentCode));
    }
}
