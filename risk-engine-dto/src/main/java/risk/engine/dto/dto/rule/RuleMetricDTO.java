package risk.engine.dto.dto.rule;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/14 15:55
 * @Version: 1.0
 */
@Data
public class RuleMetricDTO {

    /**
     * 1开始
     */
    private Integer serialNumber;

    /**
     * 指标code
     */
    private String metricCode;

    /**
     * 指标值
     */
    private String metricValue;

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 指标数据类型
     * @see risk.engine.dto.enums.MetricTypeEnum
     */
    private Integer metricType;

    /**
     * 指标来源
     * @see risk.engine.dto.enums.MetricSourceEnum
     */
    private Integer metricSource;

    /**
     * 操作符
     * @see risk.engine.dto.enums.OperationSymbolEnum
     */
    private Integer operationSymbol;

    /**
     * 类型1自定义 2特征
     */
    private String type;

    /**
     * 右侧指标code
     */
    private String rightMetricCode;

    /**
     * 右侧指标名称
     */
    private String rightMetricName;

    /**
     * 右侧指标数据类型
     * @see risk.engine.dto.enums.MetricTypeEnum
     */
    private Integer rightMetricType;

    /**
     * 右侧指标来源
     * @see risk.engine.dto.enums.MetricSourceEnum
     */
    private Integer rightMetricSource;

}
