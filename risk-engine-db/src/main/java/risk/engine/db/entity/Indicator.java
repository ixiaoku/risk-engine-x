package risk.engine.db.entity;

import lombok.Data;

import java.util.Date;

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
     * 特征code
     */
    private String incidentCode;

    private String indicatorCode;

    private String indicatorName;

    private String indicatorValue;

    private String indicatorDesc;

    private Byte indicatorSource;

    private Byte indicatorType;

    private String operator;

    private Date createTime;

    private Date updateTime;

}