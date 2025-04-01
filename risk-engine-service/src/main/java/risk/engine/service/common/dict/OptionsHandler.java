package risk.engine.service.common.dict;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Indicator;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.IndicatorTypeEnum;
import risk.engine.dto.enums.OperationSymbolEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IIndicatorService;

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
    private IIndicatorService indicatorService;

    /**
     * 操作符字典
     * @return 结果
     */
    @Bean("operationSymbol")
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
    @Bean("ruleStatus")
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
     * 指标类型字典
     * @return 结果
     */
    @Bean("indicatorType")
    public OptionsEnumFunction indicatorType() {
        return () -> Arrays.stream(IndicatorTypeEnum.values())
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getCode());
                    options.put("msg", e.getDesc());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 指标字典
     * @return 结果
     */
    @Bean("indicatorList")
    public OptionsDbFunction<String> indicatorList() {
        List<Indicator> incidentList = indicatorService.selectByExample(new Indicator());
        if (CollectionUtils.isEmpty(incidentList)) {
            return null;
        }
        return value -> incidentList.stream()
                .filter(i -> StringUtils.equals(value, i.getIncidentCode()))
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getIncidentCode());
                    options.put("msg", e.getIndicatorName());
                    return options;
                }).collect(Collectors.toList());
    }

    /**
     * 事件字典
     * @return 结果
     */
    @Bean("incidentList")
    public OptionsEnumFunction incidentList() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatusEnum.ONLINE.getCode());
        List<Incident> incidentList = incidentService.selectByExample(incident);
        if (CollectionUtils.isEmpty(incidentList)) {
            return null;
        }
        return () -> incidentList.stream()
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
    @Bean("incidentStatus")
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

}
