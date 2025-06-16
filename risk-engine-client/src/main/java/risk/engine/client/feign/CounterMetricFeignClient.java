package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.CounterMetricParam;
import risk.engine.dto.vo.CounterMetricVO;

/**
 * 计数器指标
 * @Author: X
 * @Date: 2025/6/16 12:31
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "CounterMetricFeignClient", path = "/metric/counter")
public interface CounterMetricFeignClient {

    @GetMapping("/list")
    PageResult<CounterMetricVO> list(@ModelAttribute CounterMetricParam param);

    @PostMapping("/insert")
    Boolean insert(@RequestBody CounterMetricParam param);

    @PostMapping("/delete")
    Boolean delete(@RequestBody CounterMetricParam param);

    @PostMapping("/update")
    Boolean update(@RequestBody CounterMetricParam param);

    @GetMapping("/detail")
    CounterMetricVO detail(@RequestParam("id") Long id);

}
