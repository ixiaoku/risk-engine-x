package risk.engine.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import risk.engine.common.grovvy.ExpressionValidator;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.GsonUtil;
import risk.engine.db.dao.RuleMapper;
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.MetricPO;
import risk.engine.db.entity.RulePO;
import risk.engine.db.entity.RuleVersionPO;
import risk.engine.db.entity.example.RuleExample;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.vo.RuleVO;
import risk.engine.service.common.cache.GuavaIncidentRuleCache;
import risk.engine.service.handler.GroovyExpressionParser;
import risk.engine.service.service.IMetricService;
import risk.engine.service.service.IRuleService;
import risk.engine.service.service.IRuleVersionService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
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

    @Resource
    private GuavaIncidentRuleCache guavaIncidentCache;

    @Resource
    private IRuleVersionService ruleVersionService;

    @Resource
    private IMetricService metricService;

    @Override
    public List<RulePO> selectByExample(RuleExample ruleExample) {
        return ruleMapper.selectByExample(ruleExample);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insert(RuleParam ruleParam) {

        ExpressionValidator.verify(ruleParam);
        RulePO rule = new RulePO();
        rule.setIncidentCode(ruleParam.getIncidentCode());
        rule.setRuleCode(ruleParam.getRuleCode());
        rule.setRuleName(ruleParam.getRuleName());
        rule.setStatus(ruleParam.getStatus());
        rule.setScore(ruleParam.getScore());
        rule.setPriority(0);
        List<RuleMetricDTO> metricDTOList = getRuleMetricDTOList(ruleParam.getIncidentCode(), ruleParam.getJsonScript());
        rule.setJsonScript(GsonUtil.toJson(metricDTOList));
        rule.setLogicScript(ruleParam.getLogicScript());
        String groovyScript = GroovyExpressionParser.parseToGroovyExpression(rule.getLogicScript(), metricDTOList);
        rule.setGroovyScript(groovyScript);
        rule.setDecisionResult(ruleParam.getDecisionResult());
        rule.setExpiryTime(ruleParam.getExpiryTime());
        rule.setLabel(ruleParam.getLabel());
        rule.setPenaltyAction(ruleParam.getPenaltyAction());
        rule.setResponsiblePerson(ruleParam.getResponsiblePerson());
        rule.setOperator("System");
        rule.setVersion(UUID.randomUUID().toString().replace("-", ""));
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        //规则版本
        RuleVersionPO ruleVersionPO = getRuleVersion(rule);
        boolean flag = ruleMapper.insert(rule) > 0 && ruleVersionService.insert(ruleVersionPO);
        if (flag) guavaIncidentCache.refreshCache();
        return flag;
    }

    private List<RuleMetricDTO> getRuleMetricDTOList(String incidentCode, String jsonScript) {
        //获取完整的特征类型和名称
        MetricPO metricQuery = new MetricPO();
        metricQuery.setIncidentCode(incidentCode);
        List<MetricPO> metricList = metricService.selectByExample(metricQuery);
        if (CollectionUtils.isEmpty(metricList)) {
            throw new RuntimeException();
        }
        Map<String, MetricPO> resultMap = metricList.stream().collect(Collectors.toMap(MetricPO::getMetricCode, Function.identity()));
        List<RuleMetricDTO> conditions = new Gson().fromJson(jsonScript, new TypeToken<List<RuleMetricDTO>>(){}.getType());
        return conditions.stream()
                .filter(i -> Objects.nonNull(resultMap.get(i.getMetricCode())))
                .map(metricDTO -> {
                    RuleMetricDTO ruleMetricDTO = new RuleMetricDTO();
                    ruleMetricDTO.setMetricCode(metricDTO.getMetricCode());
                    ruleMetricDTO.setOperationSymbol(metricDTO.getOperationSymbol());
                    ruleMetricDTO.setSerialNumber(metricDTO.getSerialNumber());
                    MetricPO metric = resultMap.get(metricDTO.getMetricCode());
                    ruleMetricDTO.setMetricType(metric.getMetricType());
                    ruleMetricDTO.setMetricName(metric.getMetricName());
                    ruleMetricDTO.setMetricSource(metric.getMetricSource());
                    ruleMetricDTO.setMetricValueType(metricDTO.getMetricValueType());
                    if (StringUtils.equals("custom", metricDTO.getMetricValueType())) {
                        ruleMetricDTO.setMetricValue(metricDTO.getMetricValue());
                        return ruleMetricDTO;
                    } else if (StringUtils.equals("metric", metricDTO.getMetricValueType())) {
                        MetricPO metricPO = resultMap.get(metricDTO.getMetricValue());
                        ruleMetricDTO.setMetricValue(metricDTO.getMetricValue());
                        ruleMetricDTO.setRightMetricCode(metricPO.getMetricCode());
                        ruleMetricDTO.setRightMetricName(metricPO.getMetricName());
                        ruleMetricDTO.setRightMetricSource(metricPO.getMetricSource());
                        ruleMetricDTO.setRightMetricType(metricPO.getMetricType());
                    }
                    return ruleMetricDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public List<RuleVO> list(RuleParam ruleParam) {
        RuleExample example = new RuleExample();
        example.setPageSize(ruleParam.getPageSize());
        example.setPageNum(ruleParam.getPageNum());
        example.setIncidentCode(ruleParam.getIncidentCode());
        example.setRuleCode(ruleParam.getRuleCode());
        example.setRuleName(ruleParam.getRuleName());
        example.setStatus(ruleParam.getStatus());
        List<RulePO> ruleList = ruleMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(ruleList)) {
            return List.of();
        }
        return ruleList.stream().map(rule -> {
            RuleVO ruleVO = new RuleVO();
            IncidentPO incident = guavaIncidentCache.getCacheIncident(rule.getIncidentCode());
            ruleVO.setId(rule.getId());
            ruleVO.setIncidentCode(rule.getIncidentCode());
            ruleVO.setIncidentName(incident.getIncidentName());
            ruleVO.setRuleCode(rule.getRuleCode());
            ruleVO.setRuleName(rule.getRuleName());
            ruleVO.setStatus(rule.getStatus());
            ruleVO.setOperator(rule.getOperator());
            ruleVO.setCreateTime(DateTimeUtil.getTimeByLocalDateTime(rule.getCreateTime()));
            ruleVO.setUpdateTime(DateTimeUtil.getTimeByLocalDateTime(rule.getUpdateTime()));
            return ruleVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean delete(RuleParam ruleParam) {
        RulePO rule = ruleMapper.selectByPrimaryKey(ruleParam.getId());
        boolean flag = ruleMapper.deleteByPrimaryKey(ruleParam.getId()) > 0 && ruleVersionService.deleteByRuleCode(rule.getRuleCode());
        if (flag) guavaIncidentCache.refreshCache();
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RuleParam ruleParam) {
        ExpressionValidator.verify(ruleParam);
        RulePO rule = new RulePO();
        rule.setId(ruleParam.getId());
        rule.setRuleCode(ruleParam.getRuleCode());
        rule.setRuleName(ruleParam.getRuleName());
        rule.setStatus(ruleParam.getStatus());
        rule.setScore(ruleParam.getScore());
        rule.setPriority(0);
        List<RuleMetricDTO> metricDTOList = getRuleMetricDTOList(ruleParam.getIncidentCode(), ruleParam.getJsonScript());
        rule.setJsonScript(GsonUtil.toJson(metricDTOList));
        rule.setLogicScript(ruleParam.getLogicScript());
        String groovyScript = GroovyExpressionParser.parseToGroovyExpression(rule.getLogicScript(), metricDTOList);
        rule.setGroovyScript(groovyScript);
        rule.setDecisionResult(ruleParam.getDecisionResult());
        rule.setExpiryTime(ruleParam.getExpiryTime());
        rule.setLabel(ruleParam.getLabel());
        rule.setOperator(ruleParam.getOperator());
        rule.setResponsiblePerson(ruleParam.getResponsiblePerson());
        rule.setPenaltyAction(ruleParam.getPenaltyAction());
        rule.setVersion(UUID.randomUUID().toString().replace("-", ""));
        rule.setUpdateTime(LocalDateTime.now());
        //规则版本
        RuleVersionPO ruleVersionPO = getRuleVersion(rule);
        boolean flag = ruleMapper.updateByPrimaryKey(rule) > 0 && ruleVersionService.insert(ruleVersionPO);
        if (flag) guavaIncidentCache.refreshCache();
        return flag;
    }

    @Override
    public RuleVO detail(Long id) {
        RulePO rule = ruleMapper.selectByPrimaryKey(id);
        if (Objects.isNull(rule)) {
            return null;
        }
        return getRuleResult(rule);
    }

    private RuleVO getRuleResult(RulePO rule) {
        RuleVO ruleVO = new RuleVO();
        ruleVO.setId(rule.getId());
        ruleVO.setIncidentCode(rule.getIncidentCode());
        ruleVO.setRuleCode(rule.getRuleCode());
        ruleVO.setRuleName(rule.getRuleName());
        ruleVO.setStatus(rule.getStatus());
        ruleVO.setScore(rule.getScore());
        ruleVO.setPriority(rule.getPriority());
        ruleVO.setGroovyScript(rule.getGroovyScript());
        ruleVO.setJsonScript(rule.getJsonScript());
        ruleVO.setLogicScript(rule.getLogicScript());
        ruleVO.setDecisionResult(rule.getDecisionResult());
        ruleVO.setExpiryTime(rule.getExpiryTime());
        ruleVO.setLabel(rule.getLabel());
        ruleVO.setPenaltyAction(rule.getPenaltyAction());
        ruleVO.setResponsiblePerson(rule.getResponsiblePerson());
        ruleVO.setOperator(rule.getOperator());
        ruleVO.setUpdateTime(DateTimeUtil.getTimeByLocalDateTime(rule.getUpdateTime()));
        ruleVO.setCreateTime(DateTimeUtil.getTimeByLocalDateTime(rule.getCreateTime()));
        return ruleVO;
    }

    private RuleVersionPO getRuleVersion(RulePO rule) {
        RuleVersionPO ruleVersionPO = new RuleVersionPO();
        ruleVersionPO.setRuleCode(rule.getRuleCode());
        ruleVersionPO.setStatus(rule.getStatus());
        ruleVersionPO.setLogicScript(rule.getLogicScript());
        ruleVersionPO.setGroovyScript(rule.getGroovyScript());
        ruleVersionPO.setJsonScript(rule.getJsonScript());
        ruleVersionPO.setVersion(rule.getVersion());
        ruleVersionPO.setOperator(rule.getOperator());
        ruleVersionPO.setCreateTime(LocalDateTime.now());
        ruleVersionPO.setUpdateTime(LocalDateTime.now());
        return ruleVersionPO;
    }

}
