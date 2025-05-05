package risk.engine.metric.counter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import risk.engine.common.util.BigDecimalNumberUtil;
import risk.engine.dto.dto.rule.CounterMetricDTO;
import risk.engine.metric.cache.GuavaCounterMetricCache;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/5/5 23:05
 * @Version: 1.0
 */
@Component
public class MetricCounterHandler {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private GuavaCounterMetricCache counterMetricCache;

    private static final long EXPIRE_SECONDS = 30 * 60; // 过期时间30分钟
    private static final DateTimeFormatter WINDOW_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");


    // 存储指标数据
    public void storeMetric(String incidentCode, String metricCode, BigDecimal value, long timestamp) {
        // 1. 获取指标元信息（这里假设已存储，可以从 Redis 或数据库获取）
        List<CounterMetricDTO> counterMetricDTOList = counterMetricCache.getMetricCache("counter:" + incidentCode);
        if (CollectionUtils.isEmpty(counterMetricDTOList)) return;
        CounterMetricDTO counterMetricDTO = counterMetricDTOList.stream().filter(e -> StringUtils.equals(e.getAttributeKey(), metricCode)).findFirst().orElse(null);
        if (counterMetricDTO == null) return;
        String windowSize = counterMetricDTO.getWindowSize();
        String aggregationType = counterMetricDTO.getAggregationType();

        // 2. 计算时间窗口标识
        long windowSizeMs = parseWindowSize(windowSize); // 解析时间窗口（例如 15m -> 900000ms）
        String windowKey = calculateWindowKey(timestamp, windowSizeMs);

        // 3. 生成 Redis 键
        String dataKey = String.format("metric:data:%s:%s:%s", incidentCode, metricCode, windowKey);

        // 4. 存储数据到 ZSet
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(dataKey, String.valueOf(value), timestamp);

        // 5. 设置过期时间
        redisTemplate.expire(dataKey, EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    public Pair<String, Set<String>> getZSetMetric(String incidentCode, String metricCode, long timestamp) {
        // 1. 获取指标元信息
        List<CounterMetricDTO> counterMetricDTOList = counterMetricCache.getMetricCache("counter:" + incidentCode);
        if (CollectionUtils.isEmpty(counterMetricDTOList)) return null;
        CounterMetricDTO counterMetricDTO = counterMetricDTOList.stream().filter(e -> StringUtils.equals(e.getAttributeKey(), metricCode)).findFirst().orElse(null);
        if (counterMetricDTO == null) return null;
        String windowSize = counterMetricDTO.getWindowSize();
        String aggregationType = counterMetricDTO.getAggregationType();
        if (StringUtils.isEmpty(windowSize) || StringUtils.isEmpty(aggregationType)) return null;
        // 2. 计算当前时间窗口
        long windowSizeMs = parseWindowSize(windowSize);
        String windowKey = calculateWindowKey(timestamp, windowSizeMs);
        // 3. 获取 ZSet 数据
        String dataKey = String.format("metric:data:%s:%s:%s", incidentCode, metricCode, windowKey);
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Set<String> values = zSetOps.range(dataKey, 0, -1);
        return Pair.of(aggregationType, values);
    }

    // 计算平均价格
    public BigDecimal getCounterMetric(String incidentCode, String metricCode, long timestamp) {
        Pair<String, Set<String>> pair = getZSetMetric(incidentCode, metricCode, timestamp);
        if (Objects.isNull(pair)) return BigDecimal.ZERO;
        return aggregationType(pair.getLeft(), pair.getRight());
    }

    /**
     * 根据聚合类型计算
     * @param aggregationType 聚合类型
     * @param values 值set
     * @return 结果
     */
    public BigDecimal aggregationType(String aggregationType, Set<String> values) {
        if (CollectionUtils.isEmpty(values)) return BigDecimal.ZERO;
        Set<BigDecimal>  metricValues = values.stream().map(BigDecimal::new).collect(Collectors.toSet());
        switch (aggregationType) {
            case "avg":
                return BigDecimalNumberUtil.avg(metricValues);
            case "sum":
                return BigDecimalNumberUtil.sum(metricValues);
            case "count":
                return BigDecimalNumberUtil.count(metricValues);
            case "max":
                return BigDecimalNumberUtil.max(metricValues);
            case "min":
                return BigDecimalNumberUtil.min(metricValues);
            default:
                return BigDecimal.ZERO;
        }
    }


    // 解析时间窗口（如 "15m" -> 900000ms）
    private long parseWindowSize(String windowSize) {
        String unit = windowSize.substring(windowSize.length() - 1);
        long value = Long.parseLong(windowSize.substring(0, windowSize.length() - 1));
        switch (unit) {
            case "s": return value * 1000;
            case "m": return value * 60 * 1000;
            case "h": return value * 60 * 60 * 1000;
            default: throw new IllegalArgumentException("Invalid window size unit: " + unit);
        }
    }

    // 计算时间窗口标识
    private String calculateWindowKey(long timestamp, long windowSizeMs) {
        long windowStart = timestamp - (timestamp % windowSizeMs);
        LocalDateTime windowTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(windowStart), ZoneId.systemDefault()
        );
        return windowTime.format(WINDOW_KEY_FORMATTER); // 格式化为 "yyyyMMddHHmm"
    }
}
