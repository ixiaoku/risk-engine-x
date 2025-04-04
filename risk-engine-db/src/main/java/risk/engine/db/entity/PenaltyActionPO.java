package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 处罚
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class PenaltyActionPO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 处罚code
     */
    private String penaltyCode;
    /**
     * 处罚名称
     */
    private String penaltyName;
    /**
     * 处罚定义
     */
    private String penaltyDef;
    /**
     * 状态 0禁用 1启用
     */
    private Integer status;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 描述
     */
    private String penaltyDescription;
    /**
     * 处罚字段定义
     */
    private String penaltyJson;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}