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
    public Boolean insert(@RequestBody RuleParam ruleParam);

    @PostMapping("/delete")
    public Boolean delete(@RequestBody RuleParam ruleParam);

    @PostMapping("/update")
    public Boolean update(@RequestBody RuleParam ruleParam);

    @GetMapping("/list")
    public PageResult<RuleVO> list(@ModelAttribute RuleParam ruleParam);

    @GetMapping("/detail")
    public RuleVO detail(@RequestParam("id") Long id);

}
