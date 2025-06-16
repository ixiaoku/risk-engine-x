package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import risk.engine.client.feign.IncidentFeignClient;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.PageResult;
import risk.engine.dto.dto.rule.MetricDTO;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.IncidentParam;
import risk.engine.dto.vo.IncidentVO;
import risk.engine.service.service.IIncidentService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 16:57
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/incident")
public class IncidentController implements IncidentFeignClient {

    @Resource
    private IIncidentService incidentService;

    @PostMapping("/insert")
    @Override
    public Boolean insert(@RequestBody IncidentParam incidentParam) {
        log.info("insert incident: {}", incidentParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(incidentParam.getIncidentCode())
                || StringUtils.isEmpty(incidentParam.getIncidentName())
                || ObjectUtils.isEmpty(incidentParam.getStatus())
                || StringUtils.isEmpty(incidentParam.getResponsiblePerson())
                || StringUtils.isEmpty(incidentParam.getDecisionResult())
        );
        ValidatorHandler.verify(ErrorCodeEnum.INCIDENT_EXIST_METRIC)
                .validateException(CollectionUtils.isEmpty(incidentParam.getMetrics()));
        return incidentService.insert(incidentParam);
    }

    @PostMapping("/delete")
    @Override
    public Boolean delete(@RequestBody IncidentParam incidentParam) {
        log.info("delete incident: {}", incidentParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(incidentParam.getId()));
        return incidentService.deleteByPrimaryKey(incidentParam.getId());
    }

    @PostMapping("/update")
    @Override
    public Boolean update(@RequestBody @Validated IncidentParam incidentParam) {
        log.info("update incident: {}", incidentParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(incidentParam.getId())
                || StringUtils.isEmpty(incidentParam.getIncidentName())
                || StringUtils.isEmpty(incidentParam.getResponsiblePerson())
                || StringUtils.isEmpty(incidentParam.getDecisionResult())
                || ObjectUtils.isEmpty(incidentParam.getStatus())
        );
        ValidatorHandler.verify(ErrorCodeEnum.INCIDENT_EXIST_METRIC)
                .validateException(CollectionUtils.isEmpty(incidentParam.getMetrics()));
        return incidentService.updateByPrimaryKey(incidentParam);
    }

    @PostMapping("/parse")
    @Override
    public List<MetricDTO> parseMetric(@RequestBody @Validated IncidentParam incidentParam) {
        log.info("parse indicator: {}", incidentParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(incidentParam.getRequestPayload()));
        return incidentService.parseMetric(incidentParam.getIncidentCode(), incidentParam.getRequestPayload());
    }

    @GetMapping("/detail")
    @Override
    public IncidentVO detail(@ModelAttribute IncidentParam incidentParam) {
        log.info("detail incident: {}", incidentParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(incidentParam.getId()));
        return incidentService.getOne(incidentParam.getId());
    }

    @GetMapping("/list")
    @Override
    public PageResult<IncidentVO> list(@ModelAttribute IncidentParam incidentParam) {
        log.info("list incident: {}", incidentParam);
        PageResult<IncidentVO> pageResult = new PageResult<>();
        List<IncidentVO> incidentList = incidentService.list(incidentParam);
        pageResult.setList(incidentList);
        pageResult.setTotal((long) incidentList.size());
        return pageResult;
    }

}
