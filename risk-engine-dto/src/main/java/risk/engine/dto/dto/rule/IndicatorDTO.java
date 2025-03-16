package risk.engine.dto.dto.rule;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/16 16:59
 * @Version: 1.0
 */
@Data
public class IndicatorDTO {

    /**
     * 特征code
     */
    private String indicatorCode;

    /**
     * 特征名称
     */
    private String indicatorName;

    /**
     * 特征数据类型
     * @see risk.engine.dto.enums.FieldTypeEnum
     */
    private Integer indicatorType;

}
