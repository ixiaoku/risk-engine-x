package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import risk.engine.common.grovvy.GroovyShellUtil;
import risk.engine.common.mq.RiskEngineProducer;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Rule;
import risk.engine.dto.dto.engine.HitRuleDTO;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.dto.enums.DecisionResultEnum;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.result.RiskEngineExecuteResult;
import risk.engine.service.handler.RiskEngineConsumer;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IRiskEngineExecuteService;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
    private RiskEngineProducer riskEngineProducer;

    @Resource
    private RiskEngineConsumer engineConsumer;
    @Autowired
    private RiskEngineConsumer riskEngineConsumer;

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
        if (CollectionUtils.isNotEmpty(hitOnlineRuleList)) {
            hitRule = hitOnlineRuleList.get(0);
            result.setDecisionResult(hitRule.getDecisionResult());
            log.info("命中上线规则：{}", hitRule.getRuleName());
        } else if (CollectionUtils.isNotEmpty(hitMockRuleList)) {
            hitRule = hitOnlineRuleList.get(0);
            result.setDecisionResult(hitRule.getDecisionResult());
            log.info("命中模拟规则：{}", hitRule.getRuleName());
        }
        Rule finalHitRule = hitRule;
        RiskExecuteEngineDTO executeEngineDTO = getRiskExecuteEngineDTO(riskEngineParam, incident.getIncidentName(), finalHitRule, hitOnlineRuleList, hitMockRuleList);
        //异步保存数据和发送mq消息 规则熔断
        CompletableFuture.runAsync(() -> {
            // 异步发消息 分消费者组监听 1mysql保存引擎执行结果并且同步es 2执行处罚 加名单以及调三方接口
            //riskEngineProducer.sendMessage("test_topic1", new Gson().toJson(executeEngineDTO));
            //搞了好长时间云服务器 mq有问题 搞不好 先这样写
            riskEngineConsumer.save(executeEngineDTO);
        }).exceptionally(ex -> {
            log.error("引擎执行 异步任务失败: {}, 异常: {}", riskEngineParam.getIncidentCode(), ex.getMessage(), ex);
            //处理失败逻辑 发送告警消息
            return null;
        });
        return result;
    }

    /**
     * 处理数据
     * @param riskEngineParam 业务参数
     * @param incidentName 事件名称
     * @param hitRule 命中规则
     * @param hitOnlineRuleList 命中上线规则
     * @param hitMOckRuleList 命中模拟规则
     * @return 结果
     */
    private RiskExecuteEngineDTO getRiskExecuteEngineDTO(RiskEngineParam riskEngineParam, String incidentName, Rule hitRule, List<Rule> hitOnlineRuleList, List<Rule> hitMOckRuleList) {
        RiskExecuteEngineDTO executeEngineDTO = new RiskExecuteEngineDTO();
        executeEngineDTO.setFlowNo(riskEngineParam.getFlowNo());
        executeEngineDTO.setRiskFlowNo(riskEngineParam.getIncidentCode() + ":" + UUID.randomUUID());
        executeEngineDTO.setRequestPayload(riskEngineParam.getRequestPayload());
        executeEngineDTO.setIncidentCode(riskEngineParam.getIncidentCode());
        executeEngineDTO.setIncidentName(incidentName);
        if (!Objects.isNull(hitRule)) {
            executeEngineDTO.setRuleCode(hitRule.getRuleCode());
            executeEngineDTO.setRuleName(hitRule.getRuleName());
            executeEngineDTO.setRuleStatus(hitRule.getStatus());
            executeEngineDTO.setRuleScore(hitRule.getScore());
            executeEngineDTO.setRuleDecisionResult(hitRule.getDecisionResult());
            executeEngineDTO.setRuleLabel(hitRule.getLabel());
            executeEngineDTO.setRulePenaltyAction(hitRule.getPenaltyAction());
            executeEngineDTO.setRuleVersion(hitRule.getVersion());
        }
        executeEngineDTO.setCreateTime(LocalDateTime.now());
        List<HitRuleDTO> hitMockRules = getHitRuleDTOList(RuleStatusEnum.MOCK.getCode(), hitMOckRuleList);
        executeEngineDTO.setHitMockRules(hitMockRules);
        List<HitRuleDTO> hitOnlineRules = getHitRuleDTOList(RuleStatusEnum.ONLINE.getCode(), hitMOckRuleList);
        executeEngineDTO.setHitOnlineRules(hitOnlineRules);
        return executeEngineDTO;
    }

    /**
     * 获取命中规则
     * @param status 状态
     * @param ruleList 规则集合
     * @return 结果
     */
    private List<HitRuleDTO> getHitRuleDTOList(Integer status, List<Rule> ruleList) {
        return ruleList.stream()
                .filter(e-> Objects.equals(e.getStatus(), status))
                .map(rule -> {
                    HitRuleDTO hitRuleDTO = new HitRuleDTO();
                    hitRuleDTO.setRuleCode(rule.getRuleCode());
                    hitRuleDTO.setRuleName(rule.getRuleName());
                    hitRuleDTO.setRuleStatus(rule.getStatus());
                    hitRuleDTO.setRuleScore(rule.getScore());
                    return hitRuleDTO;
                }).collect(Collectors.toList());
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
