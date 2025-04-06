package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 引擎执行结果
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class EngineResultPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务流水号
     */
    private String flowNo;

    /**
     * 风控流水号
     */
    private String riskFlowNo;

    /**
     * 事件编码
     */
    private String incidentCode;

    /**
     * 事件名称
     */
    private String incidentName;

    /**
     * 请求报文（原始请求数据）
     */
    private String requestPayload;

    /**
     * 基本要素（核心特征/身份信息等）
     */
    private String primaryElement;

    /**
     * 使用的指标
     */
    private String metric;

    /**
     * 扩展字段（可用于后续扩展）
     */
    private String extra;

    /**
     * 命中上线策略集合
     */
    private String hitOnlineRules;

    /**
     * 命中模拟策略集合
     */
    private String hitMockRules;

    /**
     * 命中主规则
     */
    private String primaryRule;

    /**
     * 决策结果（通过和拒绝）
     */
    private String decisionResult;

    /**
     * 决策耗时（毫秒）
     */
    private Long executionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}