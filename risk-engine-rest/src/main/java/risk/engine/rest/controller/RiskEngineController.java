package risk.engine.rest.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.result.RiskEngineExecuteResult;
import risk.engine.common.valid.ValidatorUtils;
import risk.engine.service.service.IRiskEngineExecuteService;
import risk.engine.service.service.impl.InitServiceImpl;

import javax.annotation.Resource;

/**
 * @Author:
 * @Date: 2025/3/9 21:03
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/risk")
public class RiskEngineController {

    @Resource
    private IRiskEngineExecuteService executeService;

    @Resource
    private InitServiceImpl initService;

    @PostMapping("/engine")
    public RiskEngineExecuteResult execute(@RequestBody RiskEngineParam riskEngineParam) throws Exception {

        log.info("RiskEngineController execute request：{}", new Gson().toJson(riskEngineParam));
        //不为空校验
        ValidatorUtils.EmptyThrowException()
                .validateException(riskEngineParam.getFlowNo());
        ValidatorUtils.EmptyThrowException()
                .validateException(riskEngineParam.getIncidentCode());
        ValidatorUtils.EmptyThrowException()
                .validateException(riskEngineParam.getRequestPayload());
        return executeService.execute(riskEngineParam);
    }

    @PostMapping("/init")
    public void init(@RequestBody RiskEngineParam riskEngineParam) throws Exception {
        initService.init();
    }

}
