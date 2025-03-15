package risk.engine.dto.dto.engine;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/14 15:55
 * @Version: 1.0
 */
@Data
public class RuleExpressionDTO {

    /**
     * 1开始
     */
    private Integer serialNumber;

    /**
     * 特征code
     */
    private String attributeCode;

    /**
     * 特征值
     */
    private String attributeValue;

    /**
     * 特征名称
     */
    private String attributeName;

    /**
     * 特征数据类型
     * @see risk.engine.dto.enums.FieldTypeEnum
     */
    private Integer attributeType;

    /**
     * 操作符
     * @see risk.engine.dto.enums.OperationSymbolEnum
     */
    private Integer operationSymbol;

}
