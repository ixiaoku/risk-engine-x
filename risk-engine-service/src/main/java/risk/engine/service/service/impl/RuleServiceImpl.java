package risk.engine.service.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.dao.RuleMapper;
import risk.engine.db.entity.Metric;
import risk.engine.db.entity.Rule;
import risk.engine.db.entity.RuleVersion;
import risk.engine.db.entity.example.RuleExample;
import risk.engine.dto.dto.IncidentDTO;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.vo.RuleVO;
import risk.engine.service.common.cache.GuavaStartupCache;
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
    private GuavaStartupCache guavaStartupCache;

    @Resource
    private IRuleVersionService ruleVersionService;

    @Resource
    private IMetricService indicatorService;

    @Override
    public List<Rule> selectByIncidentCode(String incidentCode) {
        return ruleMapper.selectByIncidentCode(incidentCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insert(RuleParam ruleParam) {
        Rule rule = new Rule();
        rule.setIncidentCode(ruleParam.getIncidentCode());
        rule.setRuleCode(ruleParam.getRuleCode());
        rule.setRuleName(ruleParam.getRuleName());
        rule.setStatus(ruleParam.getStatus());
        rule.setScore(ruleParam.getScore());
        List<RuleMetricDTO> indicatorDTOList = getRuleIndicatorDTOList(ruleParam.getIncidentCode(), ruleParam.getJsonScript());
        rule.setJsonScript(new Gson().toJson(indicatorDTOList));
        rule.setLogicScript(ruleParam.getLogicScript());
        String groovyScript = GroovyExpressionParser.parseToGroovyExpression(rule.getLogicScript(), indicatorDTOList);
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
        RuleVersion ruleVersion = getRuleVersion(rule);
        return ruleMapper.insert(rule) > 0 && ruleVersionService.insert(ruleVersion);
    }

    private List<RuleMetricDTO> getRuleIndicatorDTOList(String incidentCode, String jsonScript) {
        //获取完整的特征类型和名称
        Metric metricQuery = new Metric();
        metricQuery.setIncidentCode(incidentCode);
        List<Metric> metricList = indicatorService.selectByExample(metricQuery);
        if (CollectionUtils.isEmpty(metricList)) {
            throw new RuntimeException();
        }
        Map<String, Metric> resultMap = metricList.stream().collect(Collectors.toMap(Metric::getMetricCode, Function.identity()));
        List<RuleMetricDTO> conditions = new Gson().fromJson(jsonScript, new TypeToken<List<RuleMetricDTO>>(){}.getType());
        return conditions.stream()
                .filter(i -> Objects.nonNull(resultMap.get(i.getIndicatorCode())))
                .map(indicatorDTO -> {
                    RuleMetricDTO ruleMetricDTO = new RuleMetricDTO();
                    ruleMetricDTO.setIndicatorCode(indicatorDTO.getIndicatorCode());
                    ruleMetricDTO.setIndicatorValue(indicatorDTO.getIndicatorValue());
                    ruleMetricDTO.setOperationSymbol(indicatorDTO.getOperationSymbol());
                    ruleMetricDTO.setSerialNumber(indicatorDTO.getSerialNumber());
                    Metric metric = resultMap.get(indicatorDTO.getIndicatorCode());
                    ruleMetricDTO.setIndicatorType(metric.getMetricType());
                    ruleMetricDTO.setIndicatorName(metric.getMetricName());
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
        List<Rule> ruleList = ruleMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(ruleList)) {
            return List.of();
        }
        return ruleList.stream().map(rule -> {
            RuleVO ruleVO = new RuleVO();
            IncidentDTO incidentDTO = guavaStartupCache.getIncident(rule.getIncidentCode());
            ruleVO.setId(rule.getId());
            ruleVO.setIncidentCode(rule.getIncidentCode());
            ruleVO.setIncidentName(incidentDTO.getIncidentName());
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
        return ruleMapper.deleteByPrimaryKey(ruleParam.getId()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RuleParam ruleParam) {
        Rule rule = new Rule();
        rule.setId(ruleParam.getId());
        rule.setRuleCode(ruleParam.getRuleCode());
        rule.setRuleName(ruleParam.getRuleName());
        rule.setStatus(ruleParam.getStatus());
        rule.setScore(ruleParam.getScore());
        List<RuleMetricDTO> indicatorDTOList = getRuleIndicatorDTOList(ruleParam.getIncidentCode(), ruleParam.getJsonScript());
        rule.setJsonScript(new Gson().toJson(indicatorDTOList));
        rule.setLogicScript(ruleParam.getLogicScript());
        String groovyScript = GroovyExpressionParser.parseToGroovyExpression(rule.getLogicScript(), indicatorDTOList);
        rule.setGroovyScript(groovyScript);
        rule.setDecisionResult(ruleParam.getDecisionResult());
        rule.setExpiryTime(ruleParam.getExpiryTime());
        rule.setLabel(ruleParam.getLabel());
        rule.setOperator(ruleParam.getOperator());
        rule.setResponsiblePerson(ruleParam.getResponsiblePerson());
        rule.setVersion(UUID.randomUUID().toString().replace("-", ""));
        rule.setUpdateTime(LocalDateTime.now());
        //规则版本
        RuleVersion ruleVersion = getRuleVersion(rule);
        return ruleMapper.updateByPrimaryKey(rule) > 0 && ruleVersionService.insert(ruleVersion);
    }

    @Override
    public RuleVO detail(Long id) {
        Rule rule = ruleMapper.selectByPrimaryKey(id);
        if (Objects.isNull(rule)) {
            return null;
        }
        return getRuleResult(rule);
    }

    private RuleVO getRuleResult(Rule rule) {
        RuleVO ruleVO = new RuleVO();
        ruleVO.setId(rule.getId());
        ruleVO.setIncidentCode(rule.getIncidentCode());
        ruleVO.setRuleCode(rule.getRuleCode());
        ruleVO.setRuleName(rule.getRuleName());
        ruleVO.setStatus(rule.getStatus());
        ruleVO.setScore(rule.getScore());
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

    private RuleVersion getRuleVersion(Rule rule) {
        RuleVersion ruleVersion = new RuleVersion();
        ruleVersion.setRuleCode(rule.getRuleCode());
        ruleVersion.setStatus(rule.getStatus());
        ruleVersion.setLogicScript(rule.getLogicScript());
        ruleVersion.setGroovyScript(rule.getGroovyScript());
        ruleVersion.setJsonScript(rule.getJsonScript());
        ruleVersion.setVersion(rule.getVersion());
        ruleVersion.setOperator(rule.getOperator());
        ruleVersion.setCreateTime(LocalDateTime.now());
        ruleVersion.setUpdateTime(LocalDateTime.now());
        return ruleVersion;
    }


}
