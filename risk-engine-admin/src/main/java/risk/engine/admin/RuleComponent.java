package risk.engine.admin;


import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/6/11 17:54
 * @Version: 1.0
 */
@Component
public class RuleComponent extends NodeComponent {

    @Resource
    private RuleService ruleService;

    @Override
    public void process() {
        RiskContext context = this.getContextBean(RiskContext.class);
        String nodeId = this.getNodeId();
        String eventType = context.getEventType();
        Rule rule = ruleService.getRule(eventType, nodeId);
        if (rule != null) {
            boolean allPass = rule.getExpressions().stream()
                    .allMatch(expr -> GroovyUtil.evaluateExpression(expr, context));
            context.addRuleResult(rule.getRuleId(), allPass);
        } else {
            System.err.println("❌ No rule found for nodeId: " + nodeId + ", event: " + eventType);
        }
    }
}