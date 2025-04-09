package risk.engine.db.dao;

import risk.engine.db.entity.RuleVersionPO;

public interface RuleVersionMapper {

    int deleteByRuleCode(String ruleCode);

    int insert(RuleVersionPO record);

    RuleVersionPO selectByExample(RuleVersionPO record);

}