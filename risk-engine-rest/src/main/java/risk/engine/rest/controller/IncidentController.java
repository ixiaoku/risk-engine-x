package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.common.valid.ValidatorUtils;
import risk.engine.dto.param.IncidentParam;
import risk.engine.service.service.IIncidentService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 16:57
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/incident")
public class IncidentController {

    @Resource
    private IIncidentService incidentService;

    @PostMapping("/insert")
    public Boolean insertRule(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("Inserting incident rule: {}", incidentParam);
        //不为空校验 还有字段校验需要加 目前时间不够了
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getIncidentCode());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getIncidentName());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getStatus());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getOperator());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getIndicators());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getDecisionResult());
        return incidentService.insert(incidentParam);
    }

}
