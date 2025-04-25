package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import risk.engine.common.grovvy.GroovyShellUtil;
import risk.engine.common.redis.RedisUtil;
import risk.engine.components.kafka.KafkaConfig;
import risk.engine.components.kafka.RiskKafkaProducer;
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.RulePO;
import risk.engine.dto.dto.engine.EssentialElementDTO;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.dto.dto.rule.HitRuleDTO;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.RuleDecisionResultEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.vo.RiskEngineExecuteVO;
import risk.engine.metric.handler.MetricHandler;
import risk.engine.service.common.cache.GuavaIncidentRuleCache;
import risk.engine.service.service.IAlarmRecordService;
import risk.engine.service.service.IEngineExecuteService;

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
public class EngineExecuteServiceImpl implements IEngineExecuteService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RiskKafkaProducer producer;

    @Resource
    private GuavaIncidentRuleCache guavaIncidentRuleCache;

    @Resource
    private MetricHandler metricHandler;

    @Resource
    private KafkaConfig kafkaConfig;

    @Resource
    private IAlarmRecordService alarmRecordService;

    /**
     * 引擎执行 主逻辑
     * @param riskEngineParam 业务参数
     * @return 返回决策结果
     */
    @Override
    public RiskEngineExecuteVO execute(RiskEngineParam riskEngineParam) {
        //初始化引擎结果 默认通过
        long startTime = System.currentTimeMillis();
        RiskEngineExecuteVO result = new RiskEngineExecuteVO();
        result.setDecisionResult(RuleDecisionResultEnum.SUCCESS.getCode());
        try {
            //一、幂等性、获取事件和规则
            String key = riskEngineParam.getIncidentCode() + ":" + riskEngineParam.getFlowNo();
            Boolean isExisting = redisUtil.setNX(key, "EngineExecutorLock", 3600);
            if (!isExisting) {
                log.error("Duplicate request cannot be processed： {}", riskEngineParam.getIncidentCode());
                return result;
            }
            //查询事件
            IncidentPO incident = guavaIncidentRuleCache.getCacheIncident(riskEngineParam.getIncidentCode());
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
            List<RulePO> ruleList = guavaIncidentRuleCache.getCacheRuleList(riskEngineParam.getIncidentCode());
            if  (CollectionUtils.isEmpty(ruleList)) {
                log.error("Incident rule list is empty {}", riskEngineParam.getIncidentCode());
                return result;
            }
            //二、规则执行
            //上线规则 执行
            JSONObject paramMap = JSON.parseObject(riskEngineParam.getRequestPayload());
            //使用的指标
            Map<String, Object> metricMap = new HashMap<>();
            List<RulePO> onlineRuleList = ruleList.stream()
                    .filter(rule -> rule.getStatus().equals(RuleStatusEnum.ONLINE.getCode()))
                    .sorted(Comparator.comparingInt(RulePO::getScore).reversed())
                    .collect(Collectors.toList());
            List<RulePO> hitOnlineRuleList = getHitRuleList(incident.getIncidentCode(), paramMap, onlineRuleList, metricMap);
            List<RulePO> mockRuleList = ruleList.stream()
                    .filter(rule -> rule.getStatus().equals(RuleStatusEnum.MOCK.getCode()))
                    .sorted(Comparator.comparingInt(RulePO::getScore).reversed())
                    .collect(Collectors.toList());
            //优化点 模拟策略异步执行
            List<RulePO> hitMockRuleList = getHitRuleList(incident.getIncidentCode(), paramMap, mockRuleList, metricMap);
            //返回分数最高的命中策略 先返回上线的然后再看模拟的
            RulePO hitRule = null;
            if (CollectionUtils.isNotEmpty(hitMockRuleList)) {
                hitRule = hitMockRuleList.get(0);
                result.setDecisionResult(hitRule.getDecisionResult());
            } else if (CollectionUtils.isNotEmpty(hitOnlineRuleList)) {
                hitRule = hitOnlineRuleList.get(0);
                result.setDecisionResult(hitRule.getDecisionResult());
            }
            Long executionTime = System.currentTimeMillis() - startTime;
            RiskExecuteEngineDTO executeEngineDTO = getRiskExecuteEngineDTO(result, riskEngineParam, incident.getIncidentName(), paramMap,
                    hitRule, hitOnlineRuleList, hitMockRuleList, executionTime, metricMap);
            log.info("RiskEngineExecuteServiceImpl execute 耗时 :{}", executionTime);

            //三、异步保存数据和发送mq消息 规则熔断
            CompletableFuture.runAsync(() -> {
                // 异步发消息 分消费者组监听 1mysql保存引擎执行结果并且同步es 2执行处罚 加名单以及调三方接口
                producer.sendMessage(kafkaConfig.getTopic(), JSON.toJSONString(executeEngineDTO));
            }).exceptionally(ex -> {
                log.error("引擎执行 异步任务失败: {}, 异常: {}", riskEngineParam.getIncidentCode(), ex.getMessage(), ex);
                //todo 处理失败逻辑 发送告警消息 本来是打算目前mq发送失败 然后写消息表再重试 有时间再加吧
                return null;
            });
            return result;
        } catch (Exception e) {
            log.error("引擎执行 错误信息: {}, 事件code: {}, ", e.getMessage(), riskEngineParam.getIncidentCode(), e);
            alarmRecordService.insertAsync("引擎执行 错误信息 事件code:" + riskEngineParam.getIncidentCode(), ExceptionUtils.getStackTrace(e));
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
    private RiskExecuteEngineDTO getRiskExecuteEngineDTO(RiskEngineExecuteVO result, RiskEngineParam riskEngineParam, String incidentName,
                                                         JSONObject paramMap, RulePO hitRule, List<RulePO> hitOnlineRuleList,
                                                         List<RulePO> hitMOckRuleList, Long executionTime, Map<String, Object> metricMap) {
        RiskExecuteEngineDTO executeEngineDTO = new RiskExecuteEngineDTO();
        executeEngineDTO.setFlowNo(riskEngineParam.getFlowNo());
        executeEngineDTO.setRiskFlowNo(riskEngineParam.getIncidentCode() + ":" + UUID.randomUUID().toString().replace("-", "") + ":" +System.currentTimeMillis());
        executeEngineDTO.setIncidentCode(riskEngineParam.getIncidentCode());
        executeEngineDTO.setIncidentName(incidentName);
        executeEngineDTO.setExecutionTime(executionTime);
        executeEngineDTO.setRequestPayload(paramMap);
        //要素
        EssentialElementDTO essentialElementDTO = JSON.parseObject(riskEngineParam.getRequestPayload(), EssentialElementDTO.class);
        executeEngineDTO.setPrimaryElement(essentialElementDTO);
        executeEngineDTO.setMetric(metricMap);
        //是否命中规则
        if (Objects.isNull(hitRule)) {
            executeEngineDTO.setDecisionResult(result.getDecisionResult());
            return executeEngineDTO;
        }
        executeEngineDTO.setDecisionResult(hitRule.getDecisionResult());
        //命中的策略集合
        List<HitRuleDTO> hitMockRules = getHitRuleDTOList(RuleStatusEnum.MOCK.getCode(), hitMOckRuleList);
        executeEngineDTO.setHitMockRules(hitMockRules);
        List<HitRuleDTO> hitOnlineRules = getHitRuleDTOList(RuleStatusEnum.ONLINE.getCode(), hitOnlineRuleList);
        executeEngineDTO.setHitOnlineRules(hitOnlineRules);
        //命中的主规则
        HitRuleDTO hitRuleDTO = new HitRuleDTO();
        hitRuleDTO.setRuleCode(hitRule.getRuleCode());
        hitRuleDTO.setRuleName(hitRule.getRuleName());
        hitRuleDTO.setRuleStatus(hitRule.getStatus());
        hitRuleDTO.setRuleScore(hitRule.getScore());
        hitRuleDTO.setRuleLabel(hitRule.getLabel());
        hitRuleDTO.setRulePenaltyAction(hitRule.getPenaltyAction());
        hitRuleDTO.setRuleVersion(hitRule.getVersion());
        executeEngineDTO.setPrimaryRule(hitRuleDTO);
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
                    hitRuleDTO.setRulePenaltyAction(rule.getPenaltyAction());
                    hitRuleDTO.setRuleVersion(rule.getVersion());
                    hitRuleDTO.setRuleLabel(rule.getLabel());
                    return hitRuleDTO;
                }).collect(Collectors.toList());
    }

    /**
     * @param paramMap 请求参数map
     * @param ruleList 规则集合
     * @return 获取命中规则集合
     */
    private List<RulePO> getHitRuleList(String incidentCode, JSONObject paramMap, List<RulePO> ruleList, Map<String, Object> metricMap) {
        return ruleList.stream().filter(rule -> {
            try {
                List<RuleMetricDTO> ruleMetricDTOS = JSON.parseArray(rule.getJsonScript(), RuleMetricDTO.class);
                Map<String, Object> metricValueMap = metricHandler.getMetricValue(incidentCode, ruleMetricDTOS, paramMap);
                if (MapUtils.isEmpty(metricValueMap))  {
                    return false;
                }
                Script groovyScript = guavaIncidentRuleCache.getCacheScript(rule.getRuleCode());
                if (Objects.isNull(groovyScript)) {
                    log.error("Groovy 获取缓存失败");
                    return false;
                }
                metricMap.putAll(metricValueMap);
                boolean resultFlag = GroovyShellUtil.runGroovy(groovyScript, metricValueMap);
                if (resultFlag) {
                    log.info("命中规则名称：{}, 命中规则标识:{}", rule.getRuleName(), rule.getRuleCode());
                }
                return resultFlag;
            } catch (Exception e) {
                log.error("Groovy 执行失败，跳过 ruleCode: {}", rule.getRuleCode(), e);
                alarmRecordService.insertAsync("Groovy 执行失败，跳过 ruleCode: " + rule.getRuleCode(), ExceptionUtils.getStackTrace(e));
                return false;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public RiskEngineExecuteVO executeBatch(List<RiskEngineParam> riskEngineParam) {
        return null;
    }
}
