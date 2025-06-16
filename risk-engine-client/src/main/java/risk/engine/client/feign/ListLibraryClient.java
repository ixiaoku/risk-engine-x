package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.ListLibraryParam;
import risk.engine.dto.vo.ListLibraryVO;

/**
 * @Author: X
 * @Date: 2025/6/15 20:38
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "ListLibraryClient", path = "/list/library")
public interface ListLibraryClient {

    @PostMapping("/insert")
    Boolean insert(@RequestBody ListLibraryParam param);

    @PostMapping("/delete")
    Boolean delete(@RequestBody ListLibraryParam param);

    @PostMapping("/update")
    Boolean update(@RequestBody ListLibraryParam param);

    @GetMapping("/list")
    PageResult<ListLibraryVO> list(@ModelAttribute ListLibraryParam param);

    @GetMapping("/detail")
    ListLibraryVO detail(@RequestParam Long id);

}
