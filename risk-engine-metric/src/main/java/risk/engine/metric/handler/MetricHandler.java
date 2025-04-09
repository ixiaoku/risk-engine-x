package risk.engine.metric.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.redis.RedisUtil;
import risk.engine.common.util.ThreadPoolExecutorUtil;
import risk.engine.dto.constant.RedisKeyConstant;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.enums.MetricSourceEnum;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
@Component
public class MetricHandler {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ThreadPoolExecutorUtil threadPoolExecutorUtil;

    /**
     * 获取指标值（多线程并行，同步返回）
     * @param incidentCode 事件代码
     * @param metrics 指标列表
     * @param paramMap 请求参数
     * @return 指标结果
     */
    public Map<String, Object> getMetricValue(String incidentCode, List<RuleMetricDTO> metrics, Map<String, Object> paramMap) {
        if (StringUtils.isEmpty(incidentCode) || CollectionUtils.isEmpty(metrics)) {
            log.warn("IncidentCode or metrics is empty, returning empty map");
            return Collections.emptyMap();
        }

        // 结果容器
        Map<String, Object> result = new ConcurrentHashMap<>(metrics.size());
        ExecutorService executor = threadPoolExecutorUtil.getExecutor();

        // 每个指标一个任务
        List<CompletableFuture<Void>> futures = metrics.stream()
                .map(metric -> {
                    MetricSourceEnum source = MetricSourceEnum.getIncidentSourceEnumByCode(
                            Integer.parseInt(metric.getMetricSource()));
                    return CompletableFuture.runAsync(() -> {
                        long startTime = System.currentTimeMillis();
                        try {
                            switch (source) {
                                case ATTRIBUTE:
                                    Object value = paramMap.get(metric.getMetricCode());
                                    if (value != null) {
                                        result.put(metric.getMetricCode(), value);
                                    } else {
                                        log.warn("Metric {} not found in paramMap", metric.getMetricCode());
                                    }
                                    break;
                                case COUNT:
                                    String key = RedisKeyConstant.COUNTER + incidentCode + ":" + metric.getMetricCode();
                                    long count = redisUtil.getCount(key);
                                    result.put(metric.getMetricCode(), count);
                                    //加1 然后有效期一天
                                    redisUtil.increment(key, 1, Duration.ofDays(1));
                                    break;
                                case THIRD:
                                    result.put(metric.getMetricCode(), mockThirdPartyCall(metric));
                                    break;
                                case OFFLINE:
                                    result.put(metric.getMetricCode(), mockFlinkCall(metric));
                                    break;
                                default:
                                    log.warn("Unsupported metric source: {}", source);
                                    break;
                            }
                            log.debug("Metric {} processed in {}ms", metric.getMetricCode(), System.currentTimeMillis() - startTime);
                        } catch (Exception e) {
                            log.error("Failed to process metric {}: {}", metric.getMetricCode(), e.getMessage(), e);
                        }
                    }, executor);
                }).collect(Collectors.toList());

        // 等待所有任务完成，1 秒超时
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(1, TimeUnit.SECONDS)
                    .exceptionally(throwable -> {
                        log.error("Metric processing failed or timed out: {}", throwable.getMessage(), throwable);
                        return null;
                    })
                    .join();
        } catch (Exception e) {
            log.error("Error waiting for metric tasks: {}", e.getMessage(), e);
        }
        return result;
    }

    // 模拟三方调用
    private Object mockThirdPartyCall(RuleMetricDTO metric) {
        try {
            Thread.sleep(50); // 模拟 50ms 延迟
            log.debug("Third party call for metric: {}", metric.getMetricCode());
            return "thirdValue:" + metric.getMetricCode();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted during third party call for metric {}: {}", metric.getMetricCode(), e.getMessage());
            return null;
        }
    }

    // 模拟 Flink 调用
    private Object mockFlinkCall(RuleMetricDTO metric) {
        try {
            Thread.sleep(100); // 模拟 100ms 延迟
            log.debug("Flink call for metric: {}", metric.getMetricCode());
            return "flinkValue:" + metric.getMetricCode();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted during Flink call for metric {}: {}", metric.getMetricCode(), e.getMessage());
            return null;
        }
    }
}
