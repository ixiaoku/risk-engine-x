package risk.engine.service.common.dict;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import risk.engine.dto.enums.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/19 20:53
 * @Version: 1.0
 */
@Component
public class OptionsHandler {

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
     * 计数器指标类型字典
     * @return 结果
     */
    @Bean("counterMetricTypeList")
    public OptionsEnumFunction counterMetricType() {
        return () -> Arrays.stream(CounterMetricTypeEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 计数器指标时间窗口字典
     * @return 结果
     */
    @Bean("windowSizeList")
    public OptionsEnumFunction windowSize() {
        return () -> Arrays.stream(CounterWindowSizeEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 计数器指标 聚合方式字典
     * @return 结果
     */
    @Bean("aggregationTypeList")
    public OptionsEnumFunction aggregationType() {
        return () -> Arrays.stream(AggregationTypeEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 计数器指标 状态
     * @return 结果
     */
    @Bean("counterStatusList")
    public OptionsEnumFunction counterStatus() {
        return () -> Arrays.stream(CounterStatusEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }


}
