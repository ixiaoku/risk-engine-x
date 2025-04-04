package risk.engine.db.dao;

import risk.engine.db.entity.RuleVersionPO;

public interface RuleVersionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RuleVersionPO record);

    RuleVersionPO selectByPrimaryKey(Long id);

}