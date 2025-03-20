package risk.engine.dto.dto.rule;

import lombok.Data;
import risk.engine.dto.enums.IndicatorTypeEnum;

/**
 * @Author: X
 * @Date: 2025/3/16 16:59
 * @Version: 1.0
 */
@Data
public class IndicatorDTO {

    /**
     * 指标code
     */
    private String indicatorCode;

    /**
     * 指标名称
     */
    private String indicatorName;

    /**
     * 指标数据类型
     * @see IndicatorTypeEnum
     */
    private Integer indicatorType;

    /**
     * 指标描述
     */
    private String indicatorDesc;

    /**
     * 指标示例值
     */
    private String indicatorValue;

}
