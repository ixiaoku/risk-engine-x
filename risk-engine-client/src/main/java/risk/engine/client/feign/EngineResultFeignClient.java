package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
import risk.engine.dto.vo.ReplyRuleVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 风控引擎结果查询
 * @Author: X
 * @Date: 2025/6/16 12:46
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "EngineResultFeignClient", path = "/engine")
public interface EngineResultFeignClient {

    @PostMapping("/result/list")
    PageResult<EngineExecutorVO> list(@RequestBody EngineExecutorParam executorParam);

    @PostMapping("/result/dashboard")
    Map<String, BigDecimal> dashboard(@RequestBody EngineExecutorParam executorParam);

    @PostMapping("/result/replay")
    ReplyRuleVO replay(@RequestBody EngineExecutorParam executorParam);

    @PostMapping("/result/snapshot")
    EngineExecutorVO snapshot(@RequestBody EngineExecutorParam executorParam);

}
