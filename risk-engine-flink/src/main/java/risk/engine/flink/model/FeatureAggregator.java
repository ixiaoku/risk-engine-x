package risk.engine.flink.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/5/17 20:05
 * @Version: 1.0
 */
@Slf4j
public class FeatureAggregator implements AggregateFunction<IntermediateResult, FeatureResult, FeatureResult> {

    private static final Map<String, AggregationStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        STRATEGY_MAP.put("sum", new SumStrategy());
        STRATEGY_MAP.put("avg", new AvgStrategy());
        STRATEGY_MAP.put("max", new MaxStrategy());
        STRATEGY_MAP.put("min", new MinStrategy());
        STRATEGY_MAP.put("counter", new CounterStrategy());
    }

    @Override
    public FeatureResult createAccumulator() {
        return new FeatureResult("", "", 0.0, 0L, 0L, "unknown"); // 添加 aggregationType 字段
    }

    @Override
    public FeatureResult add(IntermediateResult value, FeatureResult acc) {
        log.info("【ADD】uid={}, metric={}, value={}, accBefore={}", value.getUid(), value.getMetricCode(), value.getValue(), acc);

        AggregationStrategy strategy = STRATEGY_MAP.get(value.getAggregationType());
        if (strategy == null) {
            log.warn("未知聚合类型：{}", value.getAggregationType());
            return acc;
        }

        long newCount = acc.getCount() + 1;
        double newValue = strategy.aggregate(acc.getValue(), value.getValue(), acc.getCount(), value.getValue());

        return new FeatureResult(
                value.getMetricCode(),
                value.getUid(),
                newValue,
                newCount,
                value.getWindowSizeSeconds(),
                value.getAggregationType()
        );
    }

    @Override
    public FeatureResult getResult(FeatureResult acc) {
        return acc;
    }

    @Override
    public FeatureResult merge(FeatureResult a, FeatureResult b) {
        log.info("【MERGE】uid={}, metric={}, a={}, b={}", a.getUid(), a.getMetricCode(), a, b);

        AggregationStrategy strategy = STRATEGY_MAP.get(a.getAggregationType());
        if (strategy == null) {
            log.warn("未知聚合类型（merge）：{}", a.getAggregationType());
            return a;
        }

        long mergedCount = a.getCount() + b.getCount();
        double mergedValue = strategy.aggregate(a.getValue(), b.getValue(), a.getCount(), b.getValue());

        return new FeatureResult(
                a.getMetricCode(),
                a.getUid(),
                mergedValue,
                mergedCount,
                a.getWindowSizeSeconds(),
                a.getAggregationType()
        );
    }

    // -------- 聚合策略定义 --------
    interface AggregationStrategy {
        double aggregate(double accValue, double newValue, long accCount, double rawNewValue);
    }

    static class SumStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount, double rawNewValue) {
            return accValue + newValue;
        }
    }

    static class AvgStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount, double rawNewValue) {
            long newCount = accCount + 1;
            return newCount == 0 ? 0.0 : (accValue * accCount + rawNewValue) / newCount;
        }
    }

    static class MaxStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount, double rawNewValue) {
            return accCount == 0 ? newValue : Math.max(accValue, newValue);
        }
    }

    static class MinStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount, double rawNewValue) {
            return accCount == 0 ? newValue : Math.min(accValue, newValue);
        }
    }

    static class CounterStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount, double rawNewValue) {
            return accValue + 1;
        }
    }
}