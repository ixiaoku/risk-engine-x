package risk.engine.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import risk.engine.dto.PageResult;
import risk.engine.dto.dto.rule.MetricDTO;
import risk.engine.dto.param.IncidentParam;
import risk.engine.dto.vo.IncidentVO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/6/16 13:10
 * @Version: 1.0
 */
@FeignClient(name = "${spring.application.name}", url = "${risk.rest.feignClient.url}", contextId = "IncidentFeignClient", path = "/incident")
public interface IncidentFeignClient {

    @PostMapping("/insert")
    Boolean insert(@RequestBody IncidentParam incidentParam);

    @PostMapping("/delete")
    Boolean delete(@RequestBody IncidentParam incidentParam);

    @PostMapping("/update")
    Boolean update(@RequestBody @Validated IncidentParam incidentParam);

    @PostMapping("/parse")
    List<MetricDTO> parseMetric(@RequestBody @Validated IncidentParam incidentParam);

    @GetMapping("/detail")
    IncidentVO detail(@ModelAttribute IncidentParam incidentParam);

    @GetMapping("/list")
    PageResult<IncidentVO> list(@ModelAttribute IncidentParam incidentParam);
    
}
