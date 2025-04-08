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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
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

    public Map<String, Object> getMetricValue(String incidentCode, List<RuleMetricDTO> metrics, Map<String, Object> paramMap) {
        if (StringUtils.isEmpty(incidentCode) || CollectionUtils.isEmpty(metrics)) {
            return Map.of();
        }
        // 分组按来源
        Map<String, List<RuleMetricDTO>> sourceMap = metrics.stream()
                .collect(Collectors.groupingBy(RuleMetricDTO::getMetricSource));

        // 结果容器
        Map<String, Object> result = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(sourceMap.size());
        List<Callable<Void>> tasks = new ArrayList<>();

        for (Map.Entry<String, List<RuleMetricDTO>> entry : sourceMap.entrySet()) {
            String source = entry.getKey();
            List<RuleMetricDTO> sourceMetrics = entry.getValue();
            MetricSourceEnum metricSourceEnum = MetricSourceEnum.getIncidentSourceEnumByCode(Integer.parseInt(source));
            Callable<Void> task = () -> {
                switch (metricSourceEnum) {
                    case ATTRIBUTE:
                        for (RuleMetricDTO metric : sourceMetrics) {
                            result.put(metric.getMetricCode(), paramMap.get(metric.getMetricCode()));
                        }
                        break;
                    case COUNT:
                        for (RuleMetricDTO metric : sourceMetrics) {
                            String keyStr = RedisKeyConstant.COUNTER + incidentCode + ":" + metric.getMetricCode();
                            long count = redisUtil.getCount(keyStr);
                            result.put(metric.getMetricCode(), count);
                            redisUtil.increment(keyStr, 1, Duration.ofDays(1));
                        }
                        break;
                    case THIRD:
                        for (RuleMetricDTO metric : sourceMetrics) {
                            // mock 三方服务调用
                            Object value = mockThirdPartyCall(metric);
                            result.put(metric.getMetricCode(), value);
                        }
                        break;
                    case OFFLINE:
                        for (RuleMetricDTO metric : sourceMetrics) {
                            // mock Flink 查询
                            Object value = mockFlinkCall(metric);
                            result.put(metric.getMetricCode(), value);
                        }
                        break;
                    default:
                        break;
                }
                return null;
            };
            tasks.add(task);
        }
        // 提交任务并等待执行，最多 1 秒
        try {
            List<Future<Void>> futures = threadPoolExecutorUtil.getExecutor()
                    .invokeAll(tasks, 1, TimeUnit.SECONDS);
            for (Future<Void> f : futures) {
                try {
                    f.get();
                } catch (Exception e) {
                    //log.error(e.getMessage(), e);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    // mock 示例方法
    private Object mockThirdPartyCall(RuleMetricDTO metric) {
        // 模拟调用时间
        System.out.println("三分服务调用");
        return "thirdValue:" + metric.getMetricCode();
    }

    private Object mockFlinkCall(RuleMetricDTO metric) {
        System.out.println("flink服务调用");
        return "flinkValue:" + metric.getMetricCode();
    }
}
