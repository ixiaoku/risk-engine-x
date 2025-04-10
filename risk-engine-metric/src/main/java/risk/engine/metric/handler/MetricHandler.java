package risk.engine.metric.handler;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import risk.engine.common.redis.RedisUtil;
import risk.engine.common.util.ThreadPoolExecutorUtil;
import risk.engine.dto.dto.crawler.KLineDTO;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
import risk.engine.dto.enums.MetricSourceEnum;
import risk.engine.metric.counter.MetricTradeSignalHandler;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 指标的处理过程
 * 根据指标来源
 * @Author: X
 * @Date: 2025/4/8 23:51
 * @Version: 1.0
 */
@Slf4j
@Service
public class MetricHandler {

    @Resource
    private ThreadPoolExecutorUtil threadPoolExecutorUtil;

    @Resource
    private MetricTradeSignalHandler metricTradeSignalHandler;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取指标值
     *
     * @param incidentCode 事件代码
     * @param metrics      指标列表
     * @param paramMap     参数映射
     * @return 指标值映射
     */
    public Map<String, Object> getMetricValue(String incidentCode, List<RuleMetricDTO> metrics, Map<String, Object> paramMap) {
        // 参数校验
        if (StringUtils.isEmpty(incidentCode) || CollectionUtils.isEmpty(metrics)) {
            log.error("Invalid input: incidentCode={} or metrics is empty", incidentCode);
            return Collections.emptyMap();
        }

        // 初始化结果容器
        Map<String, Object> result = new ConcurrentHashMap<>(metrics.size());

        // 按指标来源分组
        Map<Integer, List<RuleMetricDTO>> metricMap = metrics.stream()
                .collect(Collectors.groupingBy(RuleMetricDTO::getMetricSource));

        // 为每个来源分组创建一个异步任务
        List<CompletableFuture<Void>> futures = metricMap.entrySet().stream()
                .map(entry -> processMetricsForSource(incidentCode, entry.getKey(), entry.getValue(), paramMap, result))
                .collect(Collectors.toList());

        // 等待所有任务完成，设置超时
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(1, TimeUnit.SECONDS)
                    .exceptionally(throwable -> {
                        log.error("Metric processing timed out or failed: {}", throwable.getMessage(), throwable);
                        return null;
                    })
                    .join();
        } catch (Exception e) {
            log.error("Error waiting for metric tasks to complete: {}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 按来源处理指标
     *
     * @param sourceCode  指标来源代码
     * @param sourceMetrics 指标列表
     * @param paramMap    参数映射
     * @param result      结果容器
     * @return CompletableFuture
     */
    private CompletableFuture<Void> processMetricsForSource(String incidentCode, Integer sourceCode,
                                                            List<RuleMetricDTO> sourceMetrics,
                                                            Map<String, Object> paramMap, Map<String, Object> result) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();
            MetricSourceEnum source = MetricSourceEnum.getIncidentSourceEnumByCode(sourceCode);
            try {
                switch (source) {
                    case ATTRIBUTE:
                        processAttributeMetrics(sourceMetrics, paramMap, result);
                        break;
                    case COUNT:
                        processCountMetrics(incidentCode, sourceMetrics, paramMap, result);
                        break;
                    case THIRD:
                        processThirdPartyMetrics(sourceMetrics, result);
                        break;
                    case OFFLINE:
                        processOfflineMetrics(sourceMetrics, result);
                        break;
                    default:
                        log.warn("Unsupported metric source: {}", source);
                        break;
                }
                log.debug("Source {} processed {} metrics in {}ms", source, sourceMetrics.size(),
                        System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("Error processing metrics for source {}: {}", source, e.getMessage(), e);
            }
        }, threadPoolExecutorUtil.getExecutor());
    }

    /**
     * 处理 ATTRIBUTE 来源的指标
     */
    private void processAttributeMetrics(List<RuleMetricDTO> metrics, Map<String, Object> paramMap, Map<String, Object> result) {
        for (RuleMetricDTO metric : metrics) {
            try {
                String metricCode = metric.getMetricCode();
                Object value = paramMap.get(metricCode);
                if (value != null) {
                    result.put(metricCode, value);
                } else {
                    log.warn("Metric {} not found in paramMap", metricCode);
                    //result.put(metricCode, null); // 明确设置 null，避免后续判断问题
                }
            } catch (Exception e) {
                log.error("Failed to process ATTRIBUTE metric {}: {}", metric.getMetricCode(), e.getMessage(), e);
            }
        }
    }

    /**
     * 处理 COUNT 来源的指标
     */
    private void processCountMetrics(String incidentCode, List<RuleMetricDTO> metrics, Map<String, Object> paramMap, Map<String, Object> result) {
        if (IncidentCodeEnum.TRADE_QUANT_DATA.getCode().equals(incidentCode)) {
            KLineDTO kLineDTO = JSON.parseObject(JSON.toJSONString(paramMap), KLineDTO.class);
            metricTradeSignalHandler.calculateAndStoreIndicators(incidentCode, kLineDTO);
            for (RuleMetricDTO metric : metrics) {
                Object value = redisUtil.hget(incidentCode,kLineDTO.getOpenTime() + ":" + metric.getMetricCode());
                if (Objects.isNull(value)) {
                    continue;
                }
                result.put(metric.getMetricCode(), value);
            }
        }
    }

    /**
     * 处理 THIRD 来源的指标
     */
    private void processThirdPartyMetrics(List<RuleMetricDTO> metrics, Map<String, Object> result) {
        for (RuleMetricDTO metric : metrics) {
            try {
                result.put(metric.getMetricCode(), mockThirdPartyCall(metric));
            } catch (Exception e) {
                log.error("Failed to process THIRD metric {}: {}", metric.getMetricCode(), e.getMessage(), e);
            }
        }
    }

    /**
     * 处理 OFFLINE 来源的指标
     */
    private void processOfflineMetrics(List<RuleMetricDTO> metrics, Map<String, Object> result) {
        for (RuleMetricDTO metric : metrics) {
            try {
                result.put(metric.getMetricCode(), mockFlinkCall(metric));
            } catch (Exception e) {
                log.error("Failed to process OFFLINE metric {}: {}", metric.getMetricCode(), e.getMessage(), e);
            }
        }
    }

    private Object mockThirdPartyCall(RuleMetricDTO metric) {
        return "third-party-data-" + metric.getMetricCode(); // 模拟第三方调用
    }

    private Object mockFlinkCall(RuleMetricDTO metric) {
        return BigDecimal.valueOf(123.45); // 模拟 Flink 调用
    }
}