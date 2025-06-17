package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.RuleParam;
import risk.engine.dto.vo.RuleVO;

/**
 * @Author: X
 * @Date: 2025/6/16 13:15
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "RuleFeignClient", path = "/rule")
public interface RuleFeignClient {

    @PostMapping("/insert")
    Boolean insert(@RequestBody RuleParam ruleParam);

    @PostMapping("/delete")
    Boolean delete(@RequestBody RuleParam ruleParam);

    @PostMapping("/update")
    Boolean update(@RequestBody RuleParam ruleParam);

    @PostMapping("/list")
    PageResult<RuleVO> list(@RequestBody RuleParam ruleParam);

    @GetMapping("/detail")
    RuleVO detail(@RequestParam("id") Long id);

}
