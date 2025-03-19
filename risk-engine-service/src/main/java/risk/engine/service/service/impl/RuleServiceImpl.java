package risk.engine.service.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.db.dao.RuleMapper;
import risk.engine.db.entity.Rule;
import risk.engine.db.entity.example.RuleExample;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.result.RuleResult;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<Rule> selectByIncidentCode(String incidentCode) {
        return ruleMapper.selectByIncidentCode(incidentCode);
    }

    @Override
    public Boolean insert(RuleParam ruleParam) {
        Rule rule = new Rule();
        rule.setIncidentCode(ruleParam.getIncidentCode());
        rule.setRuleCode(ruleParam.getRuleCode());
        rule.setRuleName(ruleParam.getRuleName());
        rule.setStatus(ruleParam.getStatus());
        rule.setScore(ruleParam.getScore());
        rule.setGroovyScript(ruleParam.getGroovyScript());
        rule.setJsonScript(ruleParam.getJsonScript());
        rule.setLogicScript(ruleParam.getLogicScript());
        rule.setDecisionResult(ruleParam.getDecisionResult());
        rule.setExpiryTime(ruleParam.getExpiryTime());
        rule.setLabel(ruleParam.getLabel());
        rule.setPenaltyAction(ruleParam.getPenaltyAction());
        rule.setResponsiblePerson(ruleParam.getResponsiblePerson());
        rule.setOperator(ruleParam.getOperator());
        rule.setVersion(UUID.randomUUID().toString());
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        return ruleMapper.insert(rule) > 0;
    }

    @Override
    public List<RuleResult> list(RuleParam ruleParam) {
        RuleExample example = new RuleExample();
        example.setPageSize(ruleParam.getPageSize());
        example.setPageNum(ruleParam.getPageNum());
        example.setIncidentCode(ruleParam.getIncidentCode());
        example.setRuleCode(ruleParam.getRuleCode());
        example.setRuleName(ruleParam.getRuleName());
        example.setStatus(ruleParam.getStatus());
        List<Rule> ruleList = ruleMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(ruleList)) {
            return List.of();
        }
        return ruleList.stream().map(rule -> {
            RuleResult ruleResult = new RuleResult();
            ruleResult.setIncidentCode(rule.getIncidentCode());
            ruleResult.setRuleCode(rule.getRuleCode());
            ruleResult.setRuleName(rule.getRuleName());
            ruleResult.setStatus(rule.getStatus());
            ruleResult.setOperator(rule.getOperator());
            ruleResult.setCreateTime(rule.getCreateTime());
            ruleResult.setUpdateTime(rule.getUpdateTime());
            return ruleResult;
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean delete(RuleParam ruleParam) {
        return ruleMapper.deleteByPrimaryKey(ruleParam.getId()) > 0;
    }

    @Override
    public Boolean update(RuleParam ruleParam) {
        Rule rule = new Rule();
        rule.setId(ruleParam.getId());
        rule.setRuleName(ruleParam.getRuleName());
        rule.setStatus(ruleParam.getStatus());
        rule.setScore(ruleParam.getScore());
        rule.setGroovyScript(ruleParam.getGroovyScript());
        rule.setJsonScript(ruleParam.getJsonScript());
        rule.setLogicScript(ruleParam.getLogicScript());
        rule.setDecisionResult(ruleParam.getDecisionResult());
        rule.setExpiryTime(ruleParam.getExpiryTime());
        rule.setLabel(ruleParam.getLabel());
        rule.setPenaltyAction(ruleParam.getPenaltyAction());
        rule.setOperator(ruleParam.getOperator());
        rule.setResponsiblePerson(ruleParam.getResponsiblePerson());
        rule.setVersion(UUID.randomUUID().toString());
        rule.setUpdateTime(LocalDateTime.now());
        return ruleMapper.updateByPrimaryKey(rule) > 0;
    }

    @Override
    public RuleResult detail(RuleParam ruleParam) {
        Rule rule = ruleMapper.selectByPrimaryKey(ruleParam.getId());
        if (Objects.isNull(rule)) {
            return null;
        }
        RuleResult ruleResult = new RuleResult();
        ruleResult.setIncidentCode(rule.getIncidentCode());
        ruleResult.setRuleCode(rule.getRuleCode());
        ruleResult.setRuleName(rule.getRuleName());
        ruleResult.setStatus(rule.getStatus());
        ruleResult.setScore(rule.getScore());
        ruleResult.setGroovyScript(rule.getGroovyScript());
        ruleResult.setJsonScript(rule.getJsonScript());
        ruleResult.setLogicScript(rule.getLogicScript());
        ruleResult.setDecisionResult(rule.getDecisionResult());
        ruleResult.setExpiryTime(rule.getExpiryTime());
        ruleResult.setLabel(rule.getLabel());
        ruleResult.setPenaltyAction(rule.getPenaltyAction());
        ruleResult.setResponsiblePerson(rule.getResponsiblePerson());
        ruleResult.setOperator(rule.getOperator());
        ruleResult.setCreateTime(rule.getCreateTime());
        ruleResult.setUpdateTime(rule.getUpdateTime());
        return ruleResult;
    }

}
