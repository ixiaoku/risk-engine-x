package risk.engine.rest.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.vo.RiskEngineExecuteVO;
import risk.engine.service.service.IRiskEngineExecuteService;

import javax.annotation.Resource;

/**
 * 业务请求引擎
 * @Author: X
 * @Date: 2025/3/9 21:03
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/risk")
public class RiskEngineController {

    @Resource
    private IRiskEngineExecuteService executeService;

    @PostMapping("/engine")
    public RiskEngineExecuteVO execute(@RequestBody RiskEngineParam riskEngineParam) throws Exception {

        log.info("RiskEngineController execute request：{}", new Gson().toJson(riskEngineParam));
        //不为空校验
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(riskEngineParam.getFlowNo())
                        || StringUtils.isEmpty(riskEngineParam.getIncidentCode())
                        || StringUtils.isEmpty(riskEngineParam.getRequestPayload()));
        return executeService.execute(riskEngineParam);
    }

}
