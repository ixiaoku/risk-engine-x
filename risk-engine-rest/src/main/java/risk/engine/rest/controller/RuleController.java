package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import risk.engine.dto.param.DictionaryParam;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.result.ResponseResult;
import risk.engine.service.service.IDictionaryService;
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

    @Resource
    private IDictionaryService dictionaryService;

    @PostMapping("/insert")
    public ResponseResult insert(@RequestBody RuleParam ruleParam) {
        log.info("Inserting rule: {}", ruleParam);
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
    public ResponseResult detail(@RequestParam RuleParam ruleParam) {
        log.info("detail rules: {}", ruleParam);
        return ResponseResult.success(ruleService.detail(ruleParam));
    }

    @GetMapping("/options/indicators")
    public ResponseResult indicatorOptions(@RequestParam DictionaryParam dictionaryParam) {
        log.info("detail indicator: {}", dictionaryParam);
        return ResponseResult.success(dictionaryService.getList(dictionaryParam.getDictionaryKey()));
    }

    @GetMapping("/options/operations")
    public ResponseResult operationOptions(@RequestParam DictionaryParam dictionaryParam) {
        return ResponseResult.success(dictionaryService.getList(dictionaryParam.getDictionaryKey()));
    }

}
