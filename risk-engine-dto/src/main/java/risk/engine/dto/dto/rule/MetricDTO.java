package risk.engine.dto.dto.rule;

import lombok.Data;
import risk.engine.dto.enums.MetricTypeEnum;

/**
 * @Author: X
 * @Date: 2025/3/16 16:59
 * @Version: 1.0
 */
@Data
public class MetricDTO {

    /**
     * 事件code
     */
    private String incidentCode;

    /**
     * 指标code
     */
    private String metricCode;

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 指标数据类型
     * @see MetricTypeEnum
     */
    private Integer metricType;

    /**
     * 指标描述
     */
    private String metricDesc;

    /**
     * 指标来源
     */
    private Integer metricSource;

    /**
     * 指标示例值
     */
    private String sampleValue;

}
