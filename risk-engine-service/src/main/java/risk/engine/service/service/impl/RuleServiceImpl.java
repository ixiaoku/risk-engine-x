package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.RuleMapper;
import risk.engine.db.entity.Rule;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 19:33
 * @Version: 1.0
 */
@Service
public class RuleServiceImpl implements IRuleService {

    @Resource
    private RuleMapper ruleMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return ruleMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(Rule record) {
        return ruleMapper.insert(record) > 0;
    }

    @Override
    public List<Rule> selectByExample(Rule example) {
        return ruleMapper.selectByExample(example);
    }

    @Override
    public Rule selectByRuleCode(String incidentCode) {
        return ruleMapper.selectByRuleCode(incidentCode);
    }

    @Override
    public List<Rule> selectByIncidentCode(String incidentCode) {
        return ruleMapper.selectByIncidentCode(incidentCode);
    }

    @Override
    public Rule selectByPrimaryKey(Long id) {
        return ruleMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(Rule record) {
        return ruleMapper.updateByPrimaryKey(record) > 0;
    }
}
