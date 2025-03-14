package risk.engine.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.common.param.RiskEngineParam;
import risk.engine.common.valid.ValidatorUtils;
import risk.engine.db.entity.Rule;
import risk.engine.service.service.IEngineResultService;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;

/**
 * @Author:
 * @Date: 2025/3/9 21:03
 * @Version: 1.0
 */
@RestController
@RequestMapping("/risk")
public class RiskEngineController {

    @Resource
    private IRuleService ruleService;

    @Resource
    private IIncidentService incidentService;

    @Resource
    private IEngineResultService engineResultService;

    @PostMapping("/engine")
    public void execute(@RequestBody RiskEngineParam riskEngineParam) throws Exception {

        //不为空校验
        ValidatorUtils.EmptyThrowException()
                .validateException(riskEngineParam.getFlowNo());
        ValidatorUtils.EmptyThrowException()
                .validateException(riskEngineParam.getIncidentCode());
        ValidatorUtils.EmptyThrowException()
                .validateException(riskEngineParam.getRequestPayload());
        System.out.println("请求成功" + riskEngineParam.getFlowNo());
        Rule rule = ruleService.selectByPrimaryKey(1L);
    }

}
