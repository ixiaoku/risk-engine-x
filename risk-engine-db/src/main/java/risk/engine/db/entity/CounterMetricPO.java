package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;
/**
 * @Author: X
 * @Date: 2025/4/29 00:13
 * @Version: 1.0
 */
@Data
public class CounterMetricPO {
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
     * 应用事件code
     */
    private String incidentCodeList;

    /**
     * 属性指标key/分组key
     * 数组形式 多个指标作为分组条件
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
     * 窗口类型 tumbling sliding session
     * WindowTypeEnum
     */
    private String windowType;

    /**
     * groovy表达式
     */
    private String groovyScript;

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
