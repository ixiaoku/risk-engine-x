package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.common.grovvy.GroovyShellUtil;
import risk.engine.db.entity.EngineResult;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Rule;
import risk.engine.dto.enums.DecisionResultEnum;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.result.RiskEngineExecuteResult;
import risk.engine.service.service.IEngineResultService;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IRiskEngineExecuteService;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 引擎执行主逻辑
 * @Author: X
 * @Date: 2025/3/14 12:16
 * @Version: 1.0
 */
@Slf4j
@Service
public class RiskEngineExecuteServiceImpl implements IRiskEngineExecuteService {

    @Resource
    private IRuleService ruleService;

    @Resource
    private IIncidentService incidentService;

    @Resource
    private IEngineResultService engineResultService;

    @Resource
    private InitServiceImpl initService;

    /**
     * 引擎执行 主逻辑
     * @param riskEngineParam 业务参数
     * @return 返回决策结果
     */
    @Override
    public RiskEngineExecuteResult execute(RiskEngineParam riskEngineParam) {
        //初始化引擎结果 默认通过
        RiskEngineExecuteResult result = new RiskEngineExecuteResult();
        result.setDecisionResult(DecisionResultEnum.SUCCESS.getCode());
        //查询事件
        Incident incident = incidentService.selectByIncidentCode(riskEngineParam.getIncidentCode());
        if (incident == null) {
            log.error("Incident not found {}", riskEngineParam.getIncidentCode());
            return result;
        }
        //校验事件状态
        if (!IncidentStatusEnum.ONLINE.getCode().equals(incident.getStatus())) {
            log.error("Incident status is not ONLINE {}", riskEngineParam.getIncidentCode());
            return result;
        }
        //查询事件下的策略
        List<Rule> ruleList = ruleService.selectByIncidentCode(incident.getIncidentCode());
        if (CollectionUtils.isEmpty(ruleList)) {
            log.error("Rule not found {}", riskEngineParam.getIncidentCode());
            return result;
        }
        JSONObject paramMap = JSON.parseObject(riskEngineParam.getRequestPayload());
        //上线规则 分数降序
        List<Rule> onlineRuleList = ruleList.stream()
                .filter(rule -> rule.getStatus().equals(RuleStatusEnum.ONLINE.getCode()))
                .sorted(Comparator.comparingInt(Rule::getScore).reversed())
                .collect(Collectors.toList());
        List<Rule> hitOnlineRuleList = getHitRuleList(paramMap, onlineRuleList);
        //模拟规则 分数降序
        List<Rule> mockRuleList = ruleList.stream()
                .filter(rule -> rule.getStatus().equals(RuleStatusEnum.MOCK.getCode()))
                .sorted(Comparator.comparingInt(Rule::getScore).reversed())
                .collect(Collectors.toList());
        List<Rule> hitMockRuleList = getHitRuleList(paramMap, mockRuleList);
        //返回分数最高的命中策略 先返回上线的然后再看模拟的
        Rule hitRule = new Rule();
        if(CollectionUtils.isNotEmpty(hitOnlineRuleList)) {
            hitRule = hitOnlineRuleList.get(0);
            result.setDecisionResult(hitRule.getDecisionResult());
            log.info("命中上线规则：{}", hitRule.getRuleName());
        } else if(CollectionUtils.isNotEmpty(hitMockRuleList)) {
            hitRule = hitOnlineRuleList.get(0);
            result.setDecisionResult(hitRule.getDecisionResult());
            log.info("命中模拟规则：{}", hitRule.getRuleName());
        }
        insertEngineResult(riskEngineParam, incident.getIncidentName(), hitRule, hitOnlineRuleList, hitMockRuleList);
        return result;
    }

    private void insertEngineResult(RiskEngineParam riskEngineParam, String incidentName, Rule hitRule, List<Rule> hitOnlineRuleList, List<Rule> hitMOckRuleList) {
        EngineResult engineResult = new EngineResult();
        engineResult.setFlowNo(riskEngineParam.getFlowNo());
        engineResult.setRiskFlowNo(riskEngineParam.getIncidentCode() + ":" + UUID.randomUUID());
        engineResult.setRequestPayload(riskEngineParam.getRequestPayload());
        engineResult.setIncidentCode(riskEngineParam.getIncidentCode());
        engineResult.setIncidentName(incidentName);
        if (!Objects.isNull(hitRule)) {
            engineResult.setRuleCode(hitRule.getRuleCode());
            engineResult.setRuleName(hitRule.getRuleName());
            engineResult.setRuleStatus(hitRule.getStatus());
            engineResult.setRuleScore(hitRule.getScore());
            engineResult.setRuleDecisionResult(hitRule.getDecisionResult());
            engineResult.setRuleLabel(hitRule.getLabel());
            engineResult.setRulePenaltyAction(hitRule.getPenaltyAction());
            engineResult.setRuleVersion(hitRule.getVersion());
        }
        engineResult.setCreateTime(LocalDateTime.now());
        engineResult.setHitMockRules(new Gson().toJson(hitMOckRuleList));
        engineResult.setHitOnlineRules(new Gson().toJson(hitOnlineRuleList));
        engineResultService.insert(engineResult);
    }

    /**
     * @param paramMap 请求参数map
     * @param ruleList 规则集合
     * @return 获取命中规则集合
     */
    private List<Rule> getHitRuleList(JSONObject paramMap, List<Rule> ruleList) {
        return ruleList.stream().filter(rule -> GroovyShellUtil.runGroovy(rule.getGroovyScript(), paramMap)).collect(Collectors.toList());
    }

    @Override
    public RiskEngineExecuteResult executeBatch(List<RiskEngineParam> riskEngineParam) {
        return null;
    }
}
