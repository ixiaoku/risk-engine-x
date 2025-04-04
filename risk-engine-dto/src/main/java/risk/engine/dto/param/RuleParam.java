package risk.engine.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.vo.PenaltyActionVO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/14 16:57
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class RuleParam extends PageHelperQuery {

    /**
     * 主键
     */
    private Long id;

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
     * json结构指标
     */
    private List<RuleMetricDTO> metrics;

    /**
     * 配置的表达式 1 && 2 || 3
     */
    private String logicScript;

    /**
     * json结构指标
     */
    private String jsonScript;

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
    private List<PenaltyActionVO> penaltyActions;

    private String penaltyAction;

    /**
     * 责任人
     */
    private String responsiblePerson;

    /**
     * 操作人
     */
    private String operator;

}
