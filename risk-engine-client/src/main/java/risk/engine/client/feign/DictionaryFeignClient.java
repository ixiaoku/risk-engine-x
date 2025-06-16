package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import risk.engine.dto.param.DictionaryParam;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/6/16 12:41
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "DictionaryFeignClient", path = "/dict")
public interface DictionaryFeignClient {

    @GetMapping("/options/parameter")
    Map<String, Object> getDictByParameter(@ModelAttribute DictionaryParam dictionaryParam);

    @GetMapping("/options")
    Map<String, Object> getDict(@RequestParam("dictKeyList") String[] dictKeyList);

    @GetMapping("/options/db")
    Map<String, Object> getDictByDb(@ModelAttribute DictionaryParam dictionaryParam);

}
