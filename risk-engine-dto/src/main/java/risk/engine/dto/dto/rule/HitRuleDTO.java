package risk.engine.dto.dto.rule;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/15 23:38
 * @Version: 1.0
 */
@Data
public class HitRuleDTO {

    /**
     * 规则code
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则状态
     */
    private Integer ruleStatus;

    /**
     * 规则分数
     */
    private Integer ruleScore;

    /**
     * 规则版本
     */
    private String ruleVersion;

    /**
     * 规则标签
     */
    private String ruleLabel;

    /**
     * 规则处置 数组形式
     */
    private String rulePenaltyAction;

}
