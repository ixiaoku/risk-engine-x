package risk.engine.service.common.dict;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.MetricPO;
import risk.engine.db.entity.PenaltyActionPO;
import risk.engine.dto.enums.*;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IMetricService;
import risk.engine.service.service.IPenaltyActionService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/19 20:53
 * @Version: 1.0
 */
@Component
public class OptionsHandler {

    @Resource
    private IIncidentService incidentService;

    @Resource
    private IMetricService metricService;

    @Resource
    private IPenaltyActionService penaltyActionService;

    /**
     * 操作符字典
     * @return 结果
     */
    @Bean("operationSymbolList")
    public static OptionsEnumFunction OperationSymbol() {
        return () -> Arrays.stream(OperationSymbolEnum.values())
                .map(e -> {
                   Map<String, Object> options = new HashMap<>();
                   options.put("code", e.getCode());
                    options.put("msg", e.getName());
                   return options;
                }).collect(Collectors.toList());
    }

    /**
     * 规则状态字典
     * @return 结果
     */
    @Bean("ruleStatusList")
    public OptionsEnumFunction ruleStatus() {
        return () -> Arrays.stream(RuleStatusEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 决策结果
     * @return 结果
     */
    @Bean("decisionResultList")
    public OptionsEnumFunction decisionResult() {
        return () -> Arrays.stream(RuleDecisionResultEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 规则标签
     * @return 结果
     */
    @Bean("ruleLabelList")
    public OptionsEnumFunction ruleLabel() {
        return () -> Arrays.stream(RuleLabelEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 指标类型字典
     * @return 结果
     */
    @Bean("metricTypeList")
    public OptionsEnumFunction metricType() {
        return () -> Arrays.stream(MetricTypeEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 指标列表字典
     * @return 结果
     */
    @Bean("metricList")
    public OptionsDbFunction<String> metricList() {
        List<MetricPO> metricList = metricService.selectByExample(new MetricPO());
        if (CollectionUtils.isEmpty(metricList)) {
            return value -> List.of();
        }
        return value -> metricList.stream()
                .filter(i -> StringUtils.equals(value, i.getIncidentCode()))
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getMetricCode());
                    MetricTypeEnum metricTypeEnum = MetricTypeEnum.getIncidentStatusEnumByCode(e.getMetricType());
                    options.put("msg", e.getMetricName() + "(" + metricTypeEnum.getDesc() + ")");
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 事件列表字典
     * @return 结果
     */
    @Bean("incidentList")
    public OptionsEnumFunction incidentList() {
        IncidentPO incident = new IncidentPO();
        incident.setStatus(IncidentStatusEnum.ONLINE.getCode());
        List<IncidentPO> incidentList = incidentService.selectByExample(incident);
        if (CollectionUtils.isEmpty(incidentList)) {
            return List::of;
        }
        return () -> incidentList.stream()
                .filter(e -> Objects.equals(e.getStatus(), IncidentStatusEnum.ONLINE.getCode()))
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getIncidentCode());
                    options.put("msg", e.getIncidentName());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 事件状态字典
     * @return 结果
     */
    @Bean("incidentStatusList")
    public OptionsEnumFunction incidentStatus() {
        return () -> Arrays.stream(IncidentStatusEnum.values())
                .filter(e -> !Objects.equals(e.getCode(), IncidentStatusEnum.DELETED.getCode()))
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 处罚手段
     * @return 结果
     */
    @Bean("penaltyActionList")
    public OptionsEnumFunction penaltyAction() {
        PenaltyActionPO penaltyActionQuery = new PenaltyActionPO();
        penaltyActionQuery.setStatus(1);
        List<PenaltyActionPO> penaltyActionPOS = penaltyActionService.selectByExample(penaltyActionQuery);
        if (CollectionUtils.isEmpty(penaltyActionPOS)) {
            return List::of;
        }
        return () -> penaltyActionPOS.stream()
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getPenaltyCode());
                    options.put("msg", e.getPenaltyName());
                    return options;
                }).collect(Collectors.toList());
    }

}
