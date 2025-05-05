package risk.engine.metric.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.db.dao.CounterMetricMapper;
import risk.engine.db.entity.CounterMetricPO;
import risk.engine.dto.dto.rule.CounterMetricDTO;

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
public class GuavaCounterMetricCache {

    @Resource
    private CounterMetricMapper counterMetricMapper;

    private final Cache<String, List<CounterMetricDTO>> metricCache = CacheBuilder.newBuilder()
            .maximumSize(256)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    @PostConstruct
    private void loadCache() {
        ConcurrentHashMap<String, List<CounterMetricDTO>> metricMap = selectMetricPOList();
        metricCache.putAll(metricMap);
    }

    /**
     * 获取缓存 如果为空主动查库
     * @param incidentCode 事件code
     * @return 结果
     */
    public List<CounterMetricDTO> getMetricCache(String incidentCode) {
        List<CounterMetricDTO> metricDTOList = metricCache.getIfPresent(incidentCode);
        if (CollectionUtils.isNotEmpty(metricDTOList)) {
            return metricDTOList;
        }
        ConcurrentHashMap<String, List<CounterMetricDTO>> map = selectMetricPOList();
        return map.get(incidentCode);
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        CompletableFuture.runAsync(() -> {
            ConcurrentHashMap<String, List<CounterMetricDTO>> newData = selectMetricPOList();
            metricCache.invalidateAll();
            metricCache.putAll(newData);
        }).exceptionally(ex -> {
            log.error("刷新缓存 异步任务失败, 异常: {}", ex.getMessage(), ex);
            return null;
        });
    }

    /**
     * 查库缓存指标
     * @return 结果
     */
    private ConcurrentHashMap<String, List<CounterMetricDTO>> selectMetricPOList() {
        ConcurrentHashMap<String, List<CounterMetricDTO>> data = new ConcurrentHashMap<>();
        CounterMetricPO counterMetricPO = new CounterMetricPO();
        List<CounterMetricPO> metricPOList = counterMetricMapper.selectByExample(counterMetricPO);
        if (CollectionUtils.isEmpty(metricPOList)) {
            return data;
        }
        return metricPOList.stream().map(metric -> {
            CounterMetricDTO metricDTO = new CounterMetricDTO();
            metricDTO.setIncidentCode(metric.getIncidentCode());
            metricDTO.setMetricCode(metric.getMetricCode());
            metricDTO.setAggregationType(metric.getAggregationType());
            metricDTO.setAttributeKey(metric.getAttributeKey());
            metricDTO.setWindowSize(metric.getWindowSize());
            return metricDTO;
        }).collect(Collectors.groupingByConcurrent(
                dto -> "counter:" + dto.getMetricCode(),
                ConcurrentHashMap::new,
                Collectors.toList()
        ));
    }
}
