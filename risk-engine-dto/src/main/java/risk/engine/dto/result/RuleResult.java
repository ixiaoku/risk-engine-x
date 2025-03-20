package risk.engine.dto.result;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: X
 * @Date: 2025/3/14 16:57
 * @Version: 1.0
 */
@Data
public class RuleResult {

    /**
     * 事件code
     */
    private String incidentCode;

    /**
     * 规则code
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则状态 状态（0：删除，1：上线，2：下线，3：模拟）
     */
    private Integer status;

    /**
     * 规则分数
     */
    private Integer score;

    /**
     * groovy可执行的表达式
     */
    private String groovyScript;

    /**
     * json结构指标
     */
    private String jsonScript;

    /**
     * 配置的逻辑表达式 1 && 2 || 3
     */
    private String logicScript;

    /**
     * 决策结果
     */
    private String decisionResult;

    /**
     * 过期时间 单位h
     */
    private Integer expiryTime;

    /**
     * 标签
     */
    private String label;

    /**
     * 处置动作
     */
    private String penaltyAction;

    /**
     * 责任人
     */
    private String responsiblePerson;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

}
