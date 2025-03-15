package risk.engine.dto.dto.engine;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 20:17
 * @Version: 1.0
 */
@Data
public class RiskExecuteEngineDTO {

    /**
     * 业务方唯一id
     */
    private String flowNo;

    /**
     * 风控系统唯一id
     */
    private String riskFlowNo;

    /**
     * 请求报文
     */
    private String requestPayload;

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
     * 规则状态
     */
    private Integer ruleStatus;

    /**
     * 规则分数
     */
    private Integer ruleScore;

    /**
     * 规则处置方式
     */
    private String ruleDecisionResult;

    /**
     * 规则标签
     */
    private String ruleLabel;

    /**
     * 规则处罚
     */
    private String rulePenaltyAction;

    /**
     * 规则版本
     */
    private String ruleVersion;

    /**
     * 命中上线策略集合
     */
    private List<HitRuleDTO> hitOnlineRules;

    /**
     * 命中模拟策略集合
     */
    private List<HitRuleDTO> hitMockRules;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
