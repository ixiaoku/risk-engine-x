package risk.engine.dto.param;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: X
 * @Date: 2025/4/29 00:13
 * @Version: 1.0
 */
@Data
public class CounterMetricParam extends PageHelperQuery {
    /**
     * 主键，自增
     */
    private Long id;

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
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 指标描述
     */
    private String description;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
