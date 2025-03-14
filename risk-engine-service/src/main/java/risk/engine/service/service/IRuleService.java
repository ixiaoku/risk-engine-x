package risk.engine.service.service;

import risk.engine.db.entity.Rule;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 19:32
 * @Version: 1.0
 */
public interface IRuleService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(Rule record);

    List<Rule> selectByExample(Rule example);

    Rule selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(Rule record);

}
