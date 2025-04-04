package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 指标
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class Metric {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 指标code
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
     * 指标值 样例
     */
    private String sampleValue;

    /**
     * 指标描述
     */
    private String metricDesc;

    /**
     * 指标来源
     */
    private Integer metricSource;

    /**
     * 指标类型
     */
    private Integer metricType;

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