package risk.engine.admin;

import com.yomahub.liteflow.slot.DefaultContext;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/6/11 15:56
 * @Version: 1.0
 */
@Data
public class RiskContext extends DefaultContext {
    private String eventType;
    private Map<String, Object> data;
    private Map<String, Boolean> ruleResults = new HashMap<>();
    private Map<String, Rule> ruleMap = new HashMap<>();

    public void addRuleResult(String ruleId, Boolean result) { ruleResults.put(ruleId, result); }
    public Map<String, Boolean> getRuleResults() { return ruleResults; }
}
