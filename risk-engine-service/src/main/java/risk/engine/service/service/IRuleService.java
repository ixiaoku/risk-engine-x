package risk.engine.service.service;

import risk.engine.db.entity.RulePO;
import risk.engine.db.entity.example.RuleExample;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.vo.RuleVO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 19:32
 * @Version: 1.0
 */
public interface IRuleService {

    List<RulePO> selectByExample(RuleExample ruleExample);

    Boolean insert(RuleParam ruleParam);

    PageResult<RuleVO> list(RuleParam ruleParam);

    Boolean delete(RuleParam ruleParam);

    Boolean update(RuleParam ruleParam);

    RuleVO detail(Long id);
}
