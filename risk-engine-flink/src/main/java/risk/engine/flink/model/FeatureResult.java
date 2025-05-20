package risk.engine.flink.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: X
 * @Date: 2025/5/17 18:03
 * @Version: 1.0
 */
@Data
public class FeatureResult implements Serializable {
    private String metricCode;
    private String uid;
    private double value;
    private long count;
    private long windowSizeSeconds;
    private String aggregationType;

    public FeatureResult(String metricCode, String uid, double value, long count, long windowSizeSeconds, String aggregationType) {
        this.metricCode = metricCode;
        this.uid = uid;
        this.value = value;
        this.count = count;
        this.windowSizeSeconds = windowSizeSeconds;
        this.aggregationType = aggregationType;
    }
}