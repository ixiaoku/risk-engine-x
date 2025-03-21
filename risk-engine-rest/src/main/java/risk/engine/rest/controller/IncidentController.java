package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.common.function.ValidatorUtils;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.IncidentParam;
import risk.engine.dto.result.IncidentResult;
import risk.engine.dto.result.ResponseResult;
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
    public ResponseResult insert(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("insert incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getIncidentCode());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getIncidentName());
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getStatus());
        return ResponseResult.success(incidentService.insert(incidentParam));
    }

    @PostMapping("/delete")
    public ResponseResult delete(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("delete incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getId());
        return ResponseResult.success(incidentService.deleteByPrimaryKey(incidentParam.getId()));
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("update incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getId());
        return ResponseResult.success(incidentService.updateByPrimaryKey(incidentParam));
    }

    @PostMapping("/detail")
    public ResponseResult getOne(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("detail incident: {}", incidentParam);
        ValidatorUtils.EmptyThrowException().validateException(incidentParam.getId());
        return ResponseResult.success(incidentService.selectByPrimaryKey(incidentParam.getId()));
    }

    @PostMapping("/list")
    public ResponseResult list(@RequestBody IncidentParam incidentParam) throws Exception {
        log.info("list incident: {}", incidentParam);
        PageResult<IncidentResult> pageResult = new PageResult<>();
        List<IncidentResult> incidentList = incidentService.list(incidentParam);
        pageResult.setList(incidentList);
        pageResult.setTotal(incidentList.size());
        pageResult.setPageSize(10);
        pageResult.setPageNum(1);
        return ResponseResult.success(pageResult);
    }

}
