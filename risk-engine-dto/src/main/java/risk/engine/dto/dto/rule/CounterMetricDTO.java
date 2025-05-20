package risk.engine.dto.dto.rule;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/16 16:59
 * @Version: 1.0
 */
@Data
public class CounterMetricDTO {

    /**
     * 事件编码
     */
    private String incidentCode;

    /**
     * 属性指标key
     */
    private String attributeKey;

    /**
     * 时间滑动窗口 CounterWindowSizeEnum
     */
    private String windowSize;

    /**
     * 聚合方式 AggregationTypeEnum
     */
    private String aggregationType;

    /**
     * 计数器指标编码
     */
    private String metricCode;

    /**
     * 计数器指标名称
     */
    private String metricName;

    /**
     * 计数器指标类型 CounterMetricTypeEnum
     */
    private Integer metricType;

}
