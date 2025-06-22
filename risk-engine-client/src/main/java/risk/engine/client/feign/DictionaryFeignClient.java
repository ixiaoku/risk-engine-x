package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import risk.engine.dto.param.DictionaryParam;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/6/16 12:41
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "DictionaryFeignClient", path = "/dict")
public interface DictionaryFeignClient {
    
    @PostMapping("/options")
    Map<String, Object> getDict(@RequestBody DictionaryParam dictionaryParam);

    @PostMapping("/db")
    Map<String, Object> getDictDb(@RequestBody DictionaryParam dictionaryParam);

}
