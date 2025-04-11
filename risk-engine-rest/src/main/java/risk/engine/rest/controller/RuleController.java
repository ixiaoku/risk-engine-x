package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.vo.ResponseVO;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/14 16:55
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/rule")
public class RuleController {

    @Resource
    private IRuleService ruleService;

    @PostMapping("/insert")
    public ResponseVO insert(@RequestBody RuleParam ruleParam) throws Exception {
        log.info("Inserting rule: {}", ruleParam);
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getIncidentCode());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getRuleCode());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getRuleName());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getJsonScript());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getLogicScript());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getDecisionResult());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getMetrics());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getResponsiblePerson());

        return ResponseVO.success(ruleService.insert(ruleParam));
    }

    @PostMapping("/list")
    public ResponseVO list(@RequestBody RuleParam ruleParam) {
        log.info("list rules: {}", ruleParam);
        return ResponseVO.success(ruleService.list(ruleParam));
    }

    @PostMapping("/delete")
    public ResponseVO delete(@RequestBody RuleParam ruleParam) {
        log.info("delete rules: {}", ruleParam);
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getId());
        return ResponseVO.success(ruleService.delete(ruleParam));
    }

    @PostMapping("/update")
    public ResponseVO update(@RequestBody RuleParam ruleParam) {
        log.info("update rules: {}", ruleParam);
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getId());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getRuleName());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getJsonScript());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getLogicScript());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getDecisionResult());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getMetrics());
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ruleParam.getResponsiblePerson());
        return ResponseVO.success(ruleService.update(ruleParam));
    }

    @GetMapping("/detail")
    public ResponseVO detail(@RequestParam("id") Long id) {
        log.info("detail rules: {}", id);
        ValidatorHandler.EmptyThrowException(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(id);
        return ResponseVO.success(ruleService.detail(id));
    }

}
