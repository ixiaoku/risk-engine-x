package risk.engine.dto.dto.engine;

import lombok.Data;
import risk.engine.dto.dto.rule.HitRuleDTO;

import java.util.List;
import java.util.Map;

/**
 * 风控引擎结果
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
     * 事件
     */
    private String incidentCode;

    /**
     * 事件名称
     */
    private String incidentName;

    /**
     * 命中主规则
     */
    private HitRuleDTO primaryRule;

    /**
     * 风控结果
     */
    private String decisionResult;

    /**
     * 引擎执行耗时 单位ms
     */
    private Long executionTime;

    /**
     * 核心要素
     */
    private EssentialElementDTO primaryElement;

    /**
     * 需求扩展字段
     */
    private Map<String, Object> extra;

    /**
     * 请求报文
     */
    private Map<String, Object> requestPayload;

    /**
     * 使用的指标
     */
    private Map<String, Object> metric;

    /**
     * 命中上线策略集合
     */
    private List<HitRuleDTO> hitOnlineRules;

    /**
     * 命中模拟策略集合
     */
    private List<HitRuleDTO> hitMockRules;

    /**
     * kafka消费的唯一key
     */
    private String originalKey;

    /**
     * 创建时间
     */
    private String createTime;

}
