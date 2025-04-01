package risk.engine.db.dao;

import risk.engine.db.entity.RuleVersion;

public interface RuleVersionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RuleVersion record);

    RuleVersion selectByPrimaryKey(Long id);

}