package risk.engine.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import risk.engine.dto.param.ListLibraryParam;
import risk.engine.dto.vo.ResponseVO;

/**
 * @Author: X
 * @Date: 2025/6/15 20:38
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}")
public interface ListLibraryClient {

    @PostMapping("/insert")
    ResponseVO insert(@RequestBody ListLibraryParam param);

    @GetMapping("/list")
    ResponseVO list(@ModelAttribute ListLibraryParam param);

}
