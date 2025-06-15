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
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}")
public interface ListLibraryClient {

    @PostMapping("/insert")
    boolean insert(@RequestBody ListLibraryParam param);

    @PostMapping("/delete")
    boolean delete(@RequestBody ListLibraryParam param);

    @PostMapping("/update")
    boolean update(@RequestBody ListLibraryParam param);

    @GetMapping("/list")
    PageResult<ListLibraryVO> list(@ModelAttribute ListLibraryParam param);

    @GetMapping("/detail")
    ListLibraryVO detail(@RequestParam Long id);

}
