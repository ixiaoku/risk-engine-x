package risk.engine.dto.dto.rule;

import lombok.Data;
import risk.engine.dto.enums.MetricTypeEnum;

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
    private String indicatorCode;

    /**
     * 指标值
     */
    private String indicatorValue;

    /**
     * 指标名称
     */
    private String indicatorName;

    /**
     * 指标数据类型
     * @see MetricTypeEnum
     */
    private Integer indicatorType;

    /**
     * 操作符
     * @see risk.engine.dto.enums.OperationSymbolEnum
     */
    private Integer operationSymbol;

}
