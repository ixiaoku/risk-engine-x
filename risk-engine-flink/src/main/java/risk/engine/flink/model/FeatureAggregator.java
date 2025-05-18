package risk.engine.flink.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/5/17 20:05
 * @Version: 1.0
 */
@Slf4j
public class FeatureAggregator implements AggregateFunction<IntermediateResult, FeatureResult, FeatureResult> {

    private static final Map<String, AggregationStrategy> STRATEGY_MAP = Map.of(
            "sum", new SumStrategy(),
            "avg", new AvgStrategy(),
            "max", new MaxStrategy(),
            "min", new MinStrategy(),
            "counter", new CounterStrategy()
    );

    @Override
    public FeatureResult createAccumulator() {
        return new FeatureResult("", "", 0.0, 0L, 5 * 1000, "unknown");
    }

    @Override
    public FeatureResult add(IntermediateResult value, FeatureResult acc) {
        log.info("【ADD-BEFORE】uid={}, metric={}, accValue={}, accCount={}, newValue={}",
                acc.getUid(), acc.getMetricCode(), acc.getValue(), acc.getCount(), value.getValue());
        FeatureResult result = aggregate(value.getAggregationType(), acc, value.getValue(), value);
        log.info("【ADD-AFTER】uid={}, metric={}, accValue={}, accCount={}",
                result.getUid(), result.getMetricCode(), result.getValue(), result.getCount());
        return result;
    }

    @Override
    public FeatureResult getResult(FeatureResult acc) {
        log.debug("FeatureAggregator RESULT：{}", acc);
        return acc;
    }

    @Override
    public FeatureResult merge(FeatureResult a, FeatureResult b) {
        return aggregate(a.getAggregationType(), a, b.getValue(), null, b.getCount());
    }

    private FeatureResult aggregate(String aggTypeRaw, FeatureResult acc, double newValue, IntermediateResult origin) {
        return aggregate(aggTypeRaw, acc, newValue, origin, 1L);
    }

    private FeatureResult aggregate(String aggTypeRaw, FeatureResult acc, double newValue, IntermediateResult origin, long newCount) {
        String aggType = aggTypeRaw.toLowerCase();
        AggregationStrategy strategy = STRATEGY_MAP.get(aggType);
        if (strategy == null) {
            log.warn("Unknown aggregation type: {}", aggType);
            return acc;
        }
        long totalCount = acc.getCount() + newCount;
        double updatedValue = strategy.aggregate(acc.getValue(), newValue, acc.getCount());

        return new FeatureResult(
                origin != null ? origin.getMetricCode() : acc.getMetricCode(),
                origin != null ? origin.getUid() : acc.getUid(),
                updatedValue,
                totalCount,
                origin != null ? origin.getWindowSizeSeconds() : acc.getWindowSizeSeconds(),
                aggType
        );
    }

    interface AggregationStrategy {
        double aggregate(double accValue, double newValue, long accCount);
    }

    static class SumStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount) {
            return accValue + newValue;
        }
    }

    static class AvgStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount) {
            long newCount = accCount + 1;
            return newCount == 0 ? 0.0 : (accValue * accCount + newValue) / newCount;
        }
    }

    static class MaxStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount) {
            return accCount == 0 ? newValue : Math.max(accValue, newValue);
        }
    }

    static class MinStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount) {
            return accCount == 0 ? newValue : Math.min(accValue, newValue);
        }
    }

    static class CounterStrategy implements AggregationStrategy {
        public double aggregate(double accValue, double newValue, long accCount) {
            return accValue + 1;
        }
    }
}