package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.common.function.ValidatorUtils;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.IncidentParam;
import risk.engine.dto.vo.IncidentVO;
import risk.engine.dto.vo.ResponseVO;
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
public class IncidentController {

    @Resource
    private IIncidentService incidentService;

    @PostMapping("/insert")
    public ResponseVO insert(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("insert incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getIncidentCode());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getIncidentName());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getStatus());
        return ResponseVO.success(incidentService.insert(incidentParam));
    }

    @PostMapping("/delete")
    public ResponseVO delete(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("delete incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getId());
        return ResponseVO.success(incidentService.deleteByPrimaryKey(incidentParam.getId()));
    }

    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Validated IncidentParam incidentParam) throws Exception {
        log.info("update incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getId());
        return ResponseVO.success(incidentService.updateByPrimaryKey(incidentParam));
    }

    @PostMapping("/parse")
    public ResponseVO parseMetric(@RequestBody @Validated IncidentParam incidentParam) throws Exception {
        log.info("parse indicator: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getRequestPayload());
        return ResponseVO.success(incidentService.parseMetric(incidentParam.getIncidentCode(), incidentParam.getRequestPayload()));
    }

    @PostMapping("/detail")
    public ResponseVO getOne(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("detail incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getId());
        return ResponseVO.success(incidentService.selectByPrimaryKey(incidentParam.getId()));
    }

    @PostMapping("/list")
    public ResponseVO list(@RequestBody IncidentParam incidentParam) {
        log.info("list incident: {}", incidentParam);
        PageResult<IncidentVO> pageResult = new PageResult<>();
        List<IncidentVO> incidentList = incidentService.list(incidentParam);
        pageResult.setList(incidentList);
        pageResult.setTotal((long) incidentList.size());
        return ResponseVO.success(pageResult);
    }

}
