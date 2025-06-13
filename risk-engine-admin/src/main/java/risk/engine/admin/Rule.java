package risk.engine.admin;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/6/11 15:56
 * @Version: 1.0
 */
public class Rule {
    private String ruleId;
    private String nodeId;
    private String eventType; // 规则适用的事件类型
    private List<String> expressions;

    public Rule(String ruleId, String nodeId, String eventType, List<String> expressions) {
        this.ruleId = ruleId;
        this.nodeId = nodeId;
        this.eventType = eventType;
        this.expressions = expressions;
    }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public List<String> getExpressions() { return expressions; }
    public void setExpressions(List<String> expressions) { this.expressions = expressions; }
}
