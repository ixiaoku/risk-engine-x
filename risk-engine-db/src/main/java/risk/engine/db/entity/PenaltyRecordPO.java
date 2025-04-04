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
public class PenaltyRecordPO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 业务流水号
     */
    private String flowNo;
    /**
     * 事件code
     */
    private String incidentCode;
    /**
     * 事件名称
     */
    private String incidentName;
    /**
     * 规则code
     */
    private String ruleCode;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 处置code
     */
    private String penaltyCode;
    /**
     * 处置名称
     */
    private String penaltyName;
    /**
     * 处置接口定义
     */
    private String penaltyDef;
    /**
     * 处置原因
     */
    private String penaltyReason;
    /**
     * 处置结果
     */
    private String penaltyResult;
    /**
     * 处置描述
     */
    private String penaltyDescription;
    /**
     * 处置接口json报文
     */
    private String penaltyJson;
    /**
     * 状态 0待执行 1成功 2失败
     */
    private Integer status;
    /**
     * 重置次数
     */
    private Integer retry;
    /**
     * 处置时间
     */
    private LocalDateTime penaltyTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}