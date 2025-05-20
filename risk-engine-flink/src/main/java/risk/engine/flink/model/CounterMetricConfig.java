package risk.engine.flink.model;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/5/17 18:02
 * @Version: 1.0
 */
@Data
public class CounterMetricConfig {
    private String incidentCode;
    private String attributeKey;
    private String windowSize;
    private String aggregationType;
    private String metricCode;
    private String metricName;
    private Integer metricType;
    private Integer status;
}
