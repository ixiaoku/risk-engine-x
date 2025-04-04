package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.common.grovvy.GroovyShellUtil;
import risk.engine.components.mq.RiskEngineProducer;
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.RulePO;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.dto.dto.rule.HitRuleDTO;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.enums.RuleDecisionResultEnum;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.vo.RiskEngineExecuteVO;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IRiskEngineExecuteService;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.util.*;
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
    private RiskEngineProducer producer;

    /**
     * 引擎执行 主逻辑
     * @param riskEngineParam 业务参数
     * @return 返回决策结果
     */
    @Override
    public RiskEngineExecuteVO execute(RiskEngineParam riskEngineParam) {
        //初始化引擎结果 默认通过
        RiskEngineExecuteVO result = new RiskEngineExecuteVO();
        result.setDecisionResult(RuleDecisionResultEnum.SUCCESS.getCode());
        try {
            //查询事件
            IncidentPO incident = incidentService.selectByIncidentCode(riskEngineParam.getIncidentCode());
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
            List<RulePO> ruleList = ruleService.selectByIncidentCode(incident.getIncidentCode());
            if (CollectionUtils.isEmpty(ruleList)) {
                log.error("Rule not found {}", riskEngineParam.getIncidentCode());
                return result;
            }
            JSONObject paramMap = JSON.parseObject(riskEngineParam.getRequestPayload());
            //上线规则 分数降序
            List<RulePO> onlineRuleList = ruleList.stream()
                    .filter(rule -> rule.getStatus().equals(RuleStatusEnum.ONLINE.getCode()))
                    .sorted(Comparator.comparingInt(RulePO::getScore).reversed())
                    .collect(Collectors.toList());
            List<RulePO> hitOnlineRuleList = getHitRuleList(paramMap, onlineRuleList);
            //模拟规则 分数降序
            List<RulePO> mockRuleList = ruleList.stream()
                    .filter(rule -> rule.getStatus().equals(RuleStatusEnum.MOCK.getCode()))
                    .sorted(Comparator.comparingInt(RulePO::getScore).reversed())
                    .collect(Collectors.toList());
            List<RulePO> hitMockRuleList = getHitRuleList(paramMap, mockRuleList);
            //返回分数最高的命中策略 先返回上线的然后再看模拟的
            RulePO hitRule = new RulePO();
            if (CollectionUtils.isNotEmpty(hitOnlineRuleList)) {
                hitRule = hitOnlineRuleList.get(0);
                result.setDecisionResult(hitRule.getDecisionResult());
                log.info("命中上线规则：{}", hitRule.getRuleName());
            } else if (CollectionUtils.isNotEmpty(hitMockRuleList)) {
                hitRule = hitOnlineRuleList.get(0);
                result.setDecisionResult(hitRule.getDecisionResult());
                log.info("命中模拟规则：{}", hitRule.getRuleName());
            }
            RulePO finalHitRule = hitRule;
            RiskExecuteEngineDTO executeEngineDTO = getRiskExecuteEngineDTO(riskEngineParam, incident.getIncidentName(), paramMap, finalHitRule, hitOnlineRuleList, hitMockRuleList);
            //异步保存数据和发送mq消息 规则熔断
            CompletableFuture.runAsync(() -> {
                // 异步发消息 分消费者组监听 1mysql保存引擎执行结果并且同步es 2执行处罚 加名单以及调三方接口
                producer.sendMessage("engine_result_topic", JSON.toJSONString(executeEngineDTO));
            }).exceptionally(ex -> {
                log.error("引擎执行 异步任务失败: {}, 异常: {}", riskEngineParam.getIncidentCode(), ex.getMessage(), ex);
                //处理失败逻辑 发送告警消息 本来是打算目前mq发送失败 然后写消息表再重试 有时间再加吧
                return null;
            });
            return result;
        } catch (Exception e) {
            log.error("错误信息: {}, 事件code: {}, ", e.getMessage(), riskEngineParam.getIncidentCode(), e);
            return result;
        }
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
    private RiskExecuteEngineDTO getRiskExecuteEngineDTO(RiskEngineParam riskEngineParam, String incidentName, JSONObject paramMap, RulePO hitRule, List<RulePO> hitOnlineRuleList, List<RulePO> hitMOckRuleList) {
        RiskExecuteEngineDTO executeEngineDTO = new RiskExecuteEngineDTO();
        executeEngineDTO.setFlowNo(riskEngineParam.getFlowNo());
        executeEngineDTO.setRiskFlowNo(riskEngineParam.getIncidentCode() + ":" + UUID.randomUUID());
        executeEngineDTO.setRequestPayload(paramMap);
        executeEngineDTO.setIncidentCode(riskEngineParam.getIncidentCode());
        executeEngineDTO.setIncidentName(incidentName);
        List<HitRuleDTO> hitMockRules = getHitRuleDTOList(RuleStatusEnum.MOCK.getCode(), hitMOckRuleList);
        executeEngineDTO.setHitMockRules(hitMockRules);
        List<HitRuleDTO> hitOnlineRules = getHitRuleDTOList(RuleStatusEnum.ONLINE.getCode(), hitOnlineRuleList);
        executeEngineDTO.setHitOnlineRules(hitOnlineRules);
        if (Objects.isNull(hitRule)) {
            return executeEngineDTO;
        }
        executeEngineDTO.setRuleCode(hitRule.getRuleCode());
        executeEngineDTO.setRuleName(hitRule.getRuleName());
        executeEngineDTO.setRuleStatus(hitRule.getStatus());
        executeEngineDTO.setRuleScore(hitRule.getScore());
        executeEngineDTO.setRuleDecisionResult(hitRule.getDecisionResult());
        executeEngineDTO.setRuleLabel(hitRule.getLabel());
        executeEngineDTO.setRulePenaltyAction(hitRule.getPenaltyAction());
        executeEngineDTO.setRuleVersion(hitRule.getVersion());
        List<RuleMetricDTO> indicatorDTOList = JSON.parseArray(hitRule.getJsonScript(), RuleMetricDTO.class);
        if (CollectionUtils.isNotEmpty(indicatorDTOList)) {
            Map<String, Object> map = new HashMap<>();
            indicatorDTOList.forEach(indicatorDTO -> map.put(indicatorDTO.getMetricCode(), paramMap.get(indicatorDTO.getMetricCode())));
            executeEngineDTO.setIndicator(map);
        }
        return executeEngineDTO;
    }

    /**
     * 获取命中规则
     * @param status 状态
     * @param ruleList 规则集合
     * @return 结果
     */
    private List<HitRuleDTO> getHitRuleDTOList(Integer status, List<RulePO> ruleList) {
        return ruleList.stream()
                .filter(e-> Objects.equals(e.getStatus(), status))
                .map(rule -> {
                    HitRuleDTO hitRuleDTO = new HitRuleDTO();
                    hitRuleDTO.setRuleCode(rule.getRuleCode());
                    hitRuleDTO.setRuleName(rule.getRuleName());
                    hitRuleDTO.setRuleStatus(rule.getStatus());
                    hitRuleDTO.setRuleScore(rule.getScore());
                    hitRuleDTO.setPenaltyAction(rule.getPenaltyAction());
                    return hitRuleDTO;
                }).collect(Collectors.toList());
    }

    /**
     * @param paramMap 请求参数map
     * @param ruleList 规则集合
     * @return 获取命中规则集合
     */
    private List<RulePO> getHitRuleList(JSONObject paramMap, List<RulePO> ruleList) {
        return ruleList.stream().filter(rule -> GroovyShellUtil.runGroovy(rule.getGroovyScript(), paramMap)).collect(Collectors.toList());
    }

    @Override
    public RiskEngineExecuteVO executeBatch(List<RiskEngineParam> riskEngineParam) {
        return null;
    }
}
