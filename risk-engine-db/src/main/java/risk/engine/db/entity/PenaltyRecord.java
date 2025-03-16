package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 处置记录
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class PenaltyRecord {

    private Long id;

    private String flowNo;

    private String ruleCode;

    private String ruleName;

    private String incidentCode;

    private String incidentName;

    private String penaltyCode;

    private String penaltyName;

    private String penaltyDef;

    private String penaltyReason;

    private String penaltyResult;

    private String penaltyDescription;

    private String penaltyJson;

    private Boolean status;

    private Integer retry;

    private LocalDateTime penaltyTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}