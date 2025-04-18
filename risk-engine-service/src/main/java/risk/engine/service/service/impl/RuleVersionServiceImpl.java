package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.RuleVersionMapper;
import risk.engine.db.entity.RuleVersionPO;
import risk.engine.service.service.IRuleVersionService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/4/1 16:30
 * @Version: 1.0
 */
@Service
public class RuleVersionServiceImpl implements IRuleVersionService {

    @Resource
    private RuleVersionMapper ruleVersionMapper;

    @Override
    public boolean deleteByRuleCode(String ruleCode) {
        return ruleVersionMapper.deleteByRuleCode(ruleCode) > 0;
    }

    @Override
    public boolean insert(RuleVersionPO record) {
        return ruleVersionMapper.insert(record) > 0;
    }

    @Override
    public RuleVersionPO selectByExample(RuleVersionPO record) {
        return ruleVersionMapper.selectByExample(record);
    }
}
