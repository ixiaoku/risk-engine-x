package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import risk.engine.common.function.ValidatorUtils;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.result.ResponseResult;
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
    public ResponseResult insert(@RequestBody RuleParam ruleParam) throws Exception {
        log.info("Inserting rule: {}", ruleParam);
        ValidatorUtils.EmptyThrowException().validateException(ruleParam.getIncidentCode());
        ValidatorUtils.EmptyThrowException().validateException(ruleParam.getRuleCode());
        ValidatorUtils.EmptyThrowException().validateException(ruleParam.getRuleName());
        return ResponseResult.success(ruleService.insert(ruleParam));
    }

    @PostMapping("/list")
    public ResponseResult list(@RequestBody RuleParam ruleParam) {
        log.info("list rules: {}", ruleParam);
        return ResponseResult.success(ruleService.list(ruleParam));
    }

    @PostMapping("/delete")
    public ResponseResult delete(@RequestBody RuleParam ruleParam) {
        log.info("delete rules: {}", ruleParam);
        return ResponseResult.success(ruleService.delete(ruleParam));
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody RuleParam ruleParam) {
        log.info("update rules: {}", ruleParam);
        return ResponseResult.success(ruleService.update(ruleParam));
    }

    @GetMapping("/detail")
    public ResponseResult detail(@RequestParam("id") Long id) {
        log.info("detail rules: {}", id);
        return ResponseResult.success(ruleService.detail(id));
    }

}
