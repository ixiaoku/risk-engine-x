package risk.engine.admin;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/6/11 17:54
 * @Version: 1.0
 */
@Service
public class RuleService {

    private final List<Rule> rules = new ArrayList<>();

    @PostConstruct
    public void init() {
        rules.add(new Rule("rule1", "node1", "DEPOSIT", List.of("userKyc == 1", "amount > 10000")));
        rules.add(new Rule("rule2", "node2", "DEPOSIT", List.of("ip == '127.0.0.1'", "amount > 5000")));
        rules.add(new Rule("rule3", "node3", "WITHDRAWAL", List.of("deviceId != null", "countryCode == 'CN'")));
        rules.add(new Rule("rule4", "node4", "DEPOSIT", List.of("txId == '124rqw'", "amount <= 50000")));
    }

    public List<Rule> getAllRules() {
        return rules;
    }

    public Rule getRule(String eventType, String nodeId) {
        return rules.stream()
                .filter(r -> r.getEventType().equals(eventType) && r.getNodeId().equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    public List<String> getNodeIdsByEvent(String eventType) {
        return rules.stream()
                .filter(r -> r.getEventType().equals(eventType))
                .map(Rule::getNodeId)
                .distinct()
                .collect(Collectors.toList());
    }

    public String generateChainEL(String eventType) {
        List<String> nodeIds = getNodeIdsByEvent(eventType);
        return nodeIds.isEmpty() ? null : "THEN(" + String.join(",", nodeIds) + ")";
    }
}