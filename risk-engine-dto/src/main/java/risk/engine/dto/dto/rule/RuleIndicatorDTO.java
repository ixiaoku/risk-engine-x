package risk.engine.dto.dto.rule;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/14 15:55
 * @Version: 1.0
 */
@Data
public class RuleIndicatorDTO {

    /**
     * 1开始
     */
    private Integer serialNumber;

    /**
     * 特征code
     */
    private String indicatorCode;

    /**
     * 特征值
     */
    private String indicatorValue;

    /**
     * 特征名称
     */
    private String indicatorName;

    /**
     * 特征数据类型
     * @see risk.engine.dto.enums.FieldTypeEnum
     */
    private Integer indicatorType;

    /**
     * 操作符
     * @see risk.engine.dto.enums.OperationSymbolEnum
     */
    private Integer operationSymbol;

}
