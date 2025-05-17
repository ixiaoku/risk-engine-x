package risk.engine.flink.model;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/5/17 20:05
 * @Version: 1.0
 */
@Data
public class IntermediateResult {
    private final String metricCode;
    private final String uid;
    private final double value;
    private final long windowSizeSeconds;
    private final String aggregationType;
}
