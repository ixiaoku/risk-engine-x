package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.result.RuleResult;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.util.List;

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
    public Boolean insertRule(@RequestBody RuleParam ruleParam) {
        log.info("Inserting rule: {}", ruleParam);
        return ruleService.insert(ruleParam);
    }

    @PostMapping("/list")
    public List<RuleResult> list(@RequestBody RuleParam ruleParam) {
        log.info("list rules: {}", ruleParam);
        return ruleService.list(ruleParam);
    }

    @PostMapping("/delete")
    public Boolean delete(@RequestBody RuleParam ruleParam) {
        log.info("delete rules: {}", ruleParam);
        return ruleService.delete(ruleParam);
    }

    @PostMapping("/update")
    public Boolean update(@RequestBody RuleParam ruleParam) {
        log.info("update rules: {}", ruleParam);
        return ruleService.update(ruleParam);
    }

    @PostMapping("/detail")
    public RuleResult detail(@RequestBody RuleParam ruleParam) {
        log.info("detail rules: {}", ruleParam);
        return ruleService.detail(ruleParam);
    }

}
