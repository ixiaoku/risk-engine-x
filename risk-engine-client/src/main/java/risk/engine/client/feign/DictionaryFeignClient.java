package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/6/16 12:41
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "DictionaryFeignClient", path = "/dict")
public interface DictionaryFeignClient {

    @GetMapping("/options")
    Map<String, Object> getDict(@RequestParam("dictKeyList") String[] dictKeyList);

    @GetMapping("/db")
    Map<String, Object> getDictDb(@RequestParam("dictKeyList") String[] dictKeyList, String queryCode);

}
