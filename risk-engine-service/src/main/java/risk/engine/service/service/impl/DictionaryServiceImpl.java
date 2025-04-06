package risk.engine.service.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.MetricPO;
import risk.engine.db.entity.PenaltyActionPO;
import risk.engine.db.entity.RulePO;
import risk.engine.db.entity.example.RuleExample;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.MetricTypeEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.dto.param.DictionaryParam;
import risk.engine.service.common.dict.OptionsDbFunction;
import risk.engine.service.common.dict.OptionsEnumFunction;
import risk.engine.service.service.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/20 11:54
 * @Version: 1.0
 */
@Service
public class DictionaryServiceImpl implements IDictionaryService {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private IRuleService ruleService;

    @Resource
    private IIncidentService incidentService;

    @Resource
    private IPenaltyActionService penaltyActionService;

    @Resource
    private IMetricService metricService;

    @Override
    public Map<String, Object> getList(String[] keys) {
        Map<String, Object> result = new HashMap<>();
        for (String keyStr : keys) {
            String beanName = keyStr + "List";
            OptionsEnumFunction optionsEnumFunction = (OptionsEnumFunction) applicationContext.getBean(beanName);
            result.put(keyStr, optionsEnumFunction.getDictionary());
        }
        return result;
    }

    @Override
    public Map<String, Object> getList(String[] keys, String queryCode) {
        Map<String, Object> result = new HashMap<>();
        for (String keyStr : keys) {
            String beanName = keyStr + "List";
            OptionsDbFunction<String> optionsDbFunction = (OptionsDbFunction<String>) applicationContext.getBean(beanName);
            result.put(keyStr, optionsDbFunction.getDictionary(queryCode));
        }
        return result;
    }

    @Override
    public Map<String, Object> getDictByDB(DictionaryParam dictionaryParam) {
        Map<String, Object> result = new HashMap<>();
        for (String keyStr : dictionaryParam.getDictKeyList()) {
            switch (keyStr) {
                case "incident":
                    result.put(keyStr, incidentList());
                    break;
                case "penaltyAction":
                    result.put(keyStr, penaltyActionList());
                    break;
                case "rule":
                    result.put(keyStr, ruleList());
                    break;
                case "metric":
                    result.put(keyStr, metricList(dictionaryParam.getQueryCode()));
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private List<Map<String, Object>> incidentList() {
        IncidentPO incident = new IncidentPO();
        incident.setStatus(IncidentStatusEnum.ONLINE.getCode());
        List<IncidentPO> incidentList = incidentService.selectByExample(incident);
        if (CollectionUtils.isEmpty(incidentList)) {
            return List.of();
        }
        return incidentList.stream()
                .filter(e -> Objects.equals(e.getStatus(), IncidentStatusEnum.ONLINE.getCode()))
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getIncidentCode());
                    options.put("msg", e.getIncidentName());
                    return options;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> ruleList() {
        List<RulePO> rulePOList = ruleService.selectByExample(new RuleExample());
        if (CollectionUtils.isEmpty(rulePOList)) {
            return List.of();
        }
        return rulePOList.stream()
                .filter(e -> !Objects.equals(e.getStatus(), RuleStatusEnum.OFFLINE.getCode()))
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getRuleCode());
                    options.put("msg", e.getRuleName());
                    return options;
                }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> penaltyActionList() {
        PenaltyActionPO penaltyActionQuery = new PenaltyActionPO();
        penaltyActionQuery.setStatus(1);
        List<PenaltyActionPO> penaltyActionPOS = penaltyActionService.selectByExample(penaltyActionQuery);
        if (CollectionUtils.isEmpty(penaltyActionPOS)) {
            return List.of();
        }
        return penaltyActionPOS.stream()
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getPenaltyCode());
                    options.put("msg", e.getPenaltyName());
                    return options;
                }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> metricList(String incidentCode) {
        MetricPO metricQuery = new MetricPO();
        metricQuery.setIncidentCode(incidentCode);
        List<MetricPO> metricList = metricService.selectByExample(metricQuery);
        if (CollectionUtils.isEmpty(metricList)) {
            return List.of();
        }
        return metricList.stream()
                .map(e -> {
                    Map<String, Object> options = new HashMap<>();
                    options.put("code", e.getMetricCode());
                    MetricTypeEnum metricTypeEnum = MetricTypeEnum.getIncidentStatusEnumByCode(e.getMetricType());
                    options.put("msg", e.getMetricName() + "(" + metricTypeEnum.getDesc() + ")");
                    return options;
                }).collect(Collectors.toList());
    }

}
