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
public class Indicator {

    private Long id;

    /**
     * 指标code
     */
    private String incidentCode;

    /**
     * 指标code
     */
    private String indicatorCode;

    /**
     * 指标名称
     */
    private String indicatorName;

    /**
     * 指标值 样例
     */
    private String indicatorValue;

    /**
     * 指标描述
     */
    private String indicatorDesc;

    /**
     * 指标来源
     */
    private Integer indicatorSource;

    /**
     * 指标类型
     */
    private Integer indicatorType;

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