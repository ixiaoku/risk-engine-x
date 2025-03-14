package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.db.entity.Rule;
import risk.engine.dto.param.RuleParam;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Author: X
 * @Date: 2025/3/14 16:55
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/rule")
public class RuleController {

    @Resource
    private IRuleService ruleService;

    @PostMapping("/insert")
    public Boolean insertRule(@RequestBody RuleParam ruleParam) {
        log.info("Inserting rule: {}", ruleParam);
        Rule rule = new Rule();
        rule.setIncidentCode(ruleParam.getIncidentCode());
        rule.setRuleCode(ruleParam.getRuleCode());
        rule.setRuleName(ruleParam.getRuleName());
        rule.setStatus(ruleParam.getStatus());
        rule.setScore(ruleParam.getScore());
        rule.setGroovyScript(ruleParam.getConditionScript());
        rule.setJsonScript(ruleParam.getJsonScript());
        rule.setLogicScript(ruleParam.getExpression());
        rule.setDecisionResult(ruleParam.getDecisionResult());
        rule.setExpiryTime(ruleParam.getExpiryTime());
        rule.setLabel(ruleParam.getLabel());
        rule.setPenaltyAction(ruleParam.getPenaltyAction());
        rule.setVersion(UUID.randomUUID().toString());
        rule.setResponsiblePerson(ruleParam.getResponsiblePerson());
        rule.setOperator(ruleParam.getOperator());
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        return ruleService.insert(rule);
    }

}
