package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import risk.engine.client.feign.RuleFeignClient;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.PageResult;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.vo.RuleVO;
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
public class RuleController implements RuleFeignClient {

    @Resource
    private IRuleService ruleService;

    @PostMapping("/insert")
    @Override
    public Boolean insert(@RequestBody RuleParam ruleParam) {
        log.info("Inserting rule: {}", ruleParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(StringUtils.isEmpty(ruleParam.getIncidentCode())
                || StringUtils.isEmpty(ruleParam.getIncidentCode())
                || StringUtils.isEmpty(ruleParam.getRuleCode())
                || StringUtils.isEmpty(ruleParam.getRuleName())
                || StringUtils.isEmpty(ruleParam.getJsonScript())
                || StringUtils.isEmpty(ruleParam.getLogicScript())
                || StringUtils.isEmpty(ruleParam.getDecisionResult())
                || CollectionUtils.isEmpty(ruleParam.getMetrics())
                || StringUtils.isEmpty(ruleParam.getResponsiblePerson())
        );
        return ruleService.insert(ruleParam);
    }

    @PostMapping("/list")
    @Override
    public PageResult<RuleVO> list(@RequestBody RuleParam ruleParam) {
        log.info("list rules: {}", ruleParam);
        return ruleService.list(ruleParam);
    }

    @PostMapping("/delete")
    @Override
    public Boolean delete(@RequestBody RuleParam ruleParam) {
        log.info("delete rules: {}", ruleParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ObjectUtils.isEmpty(ruleParam.getId()));
        return ruleService.delete(ruleParam);
    }

    @PostMapping("/update")
    @Override
    public Boolean update(@RequestBody RuleParam ruleParam) {
        log.info("update rules: {}", ruleParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(ruleParam.getId())
                || StringUtils.isEmpty(ruleParam.getRuleName())
                || StringUtils.isEmpty(ruleParam.getJsonScript())
                || StringUtils.isEmpty(ruleParam.getLogicScript())
                || StringUtils.isEmpty(ruleParam.getDecisionResult())
                || CollectionUtils.isEmpty(ruleParam.getMetrics())
                || StringUtils.isEmpty(ruleParam.getResponsiblePerson())
        );
        return ruleService.update(ruleParam);
    }

    @GetMapping("/detail")
    @Override
    public RuleVO detail(@RequestParam("id") Long id) {
        log.info("detail rules: {}", id);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL).validateException(ObjectUtils.isEmpty(id));
        return ruleService.detail(id);
    }

}
