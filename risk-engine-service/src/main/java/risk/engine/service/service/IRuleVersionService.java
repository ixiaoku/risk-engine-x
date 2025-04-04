package risk.engine.service.service;

import risk.engine.db.entity.RuleVersionPO;

/**
 * @Author: X
 * @Date: 2025/4/1 16:29
 * @Version: 1.0
 */
public interface IRuleVersionService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(RuleVersionPO record);

    RuleVersionPO selectByPrimaryKey(Long id);

}
