package risk.engine.service.service;

import risk.engine.db.entity.Rule;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.result.RuleResult;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 19:32
 * @Version: 1.0
 */
public interface IRuleService {

    List<Rule> selectByIncidentCode(String incidentCode);

    Boolean insert(RuleParam ruleParam);

    List<RuleResult> list(RuleParam ruleParam);

    Boolean delete(RuleParam ruleParam);

    Boolean update(RuleParam ruleParam);

    RuleResult detail(Long id);
}
