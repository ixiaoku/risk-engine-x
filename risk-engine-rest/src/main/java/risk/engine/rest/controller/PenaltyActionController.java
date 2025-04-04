package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.dto.param.PenaltyActionParam;
import risk.engine.dto.vo.ResponseVO;
import risk.engine.service.service.IPenaltyActionService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/4/4 20:30
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/penalty")
public class PenaltyActionController {

    @Resource
    private IPenaltyActionService penaltyActionService;

    @PostMapping("/fields")
    public ResponseVO getFields(@RequestBody PenaltyActionParam penaltyActionParam) throws Exception {
        log.info("get fields: {}", penaltyActionParam);
        return ResponseVO.success(penaltyActionService.getPenaltyFields(penaltyActionParam));
    }

}
