package risk.engine.flink.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: X
 * @Date: 2025/5/17 18:03
 * @Version: 1.0
 */
@Getter
@Setter
public class FeatureResult {
    private String metricCode;
    private String uid;
    private double value;
    private long windowSizeSeconds;
    private long count;
    private String aggregationType;

    public FeatureResult() {
    }

    public FeatureResult(String metricCode, String uid, double value, long windowSizeSeconds, long count, String aggregationType) {
        this.metricCode = metricCode;
        this.uid = uid;
        this.value = value;
        this.windowSizeSeconds = windowSizeSeconds;
        this.count = count;
        this.aggregationType = aggregationType;
    }
}
