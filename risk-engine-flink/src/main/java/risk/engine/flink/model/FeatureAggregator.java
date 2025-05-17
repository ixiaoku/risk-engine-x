package risk.engine.flink.model;

/**
 * @Author: X
 * @Date: 2025/5/17 20:05
 * @Version: 1.0
 */
public class FeatureAggregator implements org.apache.flink.api.common.functions.AggregateFunction<IntermediateResult, FeatureResult, FeatureResult> {
    @Override
    public FeatureResult createAccumulator() {
        return new FeatureResult("", "", 0.0, 0);
    }

    @Override
    public FeatureResult add(IntermediateResult value, FeatureResult acc) {
        double newValue;
        long newCount = acc.getCount() + 1;
        switch (value.getAggregationType()) {
            case "sum": newValue = acc.getValue() + value.getValue(); break;
            case "avg": newValue = (acc.getValue() * acc.getCount() + value.getValue()) / newCount; break;
            case "max": newValue = Math.max(acc.getValue(), value.getValue()); break;
            case "min": newValue = Math.min(acc.getValue(), value.getValue()); break;
            case "counter": newValue = acc.getValue() + 1; break;
            default: newValue = acc.getValue();
        }
        FeatureResult result = new FeatureResult(
                value.getMetricCode(),
                value.getUid(),
                newValue,
                value.getWindowSizeSeconds()
        );
        result.setCount(newCount);
        return result;
    }

    @Override
    public FeatureResult getResult(FeatureResult acc) {
        return acc;
    }

    @Override
    public FeatureResult merge(FeatureResult a, FeatureResult b) {
        double mergedValue;
        long mergedCount = a.getCount() + b.getCount();
        switch (a.getAggregationType()) {
            case "sum": mergedValue = a.getValue() + b.getValue(); break;
            case "avg": mergedValue = (a.getValue() * a.getCount() + b.getValue() * b.getCount()) / mergedCount; break;
            case "max": mergedValue = Math.max(a.getValue(), b.getValue()); break;
            case "min": mergedValue = Math.min(a.getValue(), b.getValue()); break;
            case "counter": mergedValue = a.getValue() + b.getValue(); break;
            default: mergedValue = a.getValue();
        }
        FeatureResult result = new FeatureResult(
                a.getMetricCode(),
                a.getUid(),
                mergedValue,
                a.getWindowSizeSeconds()
        );
        result.setCount(mergedCount);
        return result;
    }
}
