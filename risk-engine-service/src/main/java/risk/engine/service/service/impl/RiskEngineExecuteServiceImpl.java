package risk.engine.service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Rule;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.result.RiskEngineExecuteResult;
import risk.engine.service.service.IEngineResultService;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IRiskEngineExecuteService;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
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

    /**
     * 引擎执行 主逻辑
     * @param riskEngineParam 业务参数
     * @return 返回决策结果
     */
    @Override
    public RiskEngineExecuteResult execute(RiskEngineParam riskEngineParam) {
        RiskEngineExecuteResult result = new RiskEngineExecuteResult();
        result.setDecisionResult("1");
        //查询事件
        Incident incident = incidentService.selectByIncidentCode(riskEngineParam.getIncidentCode());
        if (incident == null) {
            log.error("Incident not found");
            return result;
        }
        //校验事件状态
        if (!IncidentStatusEnum.ONLINE.getCode().equals(incident.getStatus())) {
            log.error("Incident status is not ONLINE");
        }
        //查询事件下的策略
        List<Rule> ruleList = ruleService.selectByIncidentCode(incident.getIncidentCode());
        if (CollectionUtils.isEmpty(ruleList)) {
            log.info("Rule not found");
        }
        //上线策略
        List<Rule> onlineRuleList = ruleList.stream().filter(rule -> rule.getStatus().equals(RuleStatusEnum.ONLINE.getCode())).distinct().collect(Collectors.toList());
        //模拟策略
        List<Rule> mockRuleList = ruleList.stream().filter(rule -> rule.getStatus().equals(RuleStatusEnum.MOCK.getCode())).distinct().collect(Collectors.toList());


        return null;
    }

    @Override
    public RiskEngineExecuteResult executeBatch(List<RiskEngineParam> riskEngineParam) {
        return null;
    }
}
