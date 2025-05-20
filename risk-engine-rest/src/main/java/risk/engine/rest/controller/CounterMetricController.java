package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.CounterMetricParam;
import risk.engine.dto.vo.ResponseVO;
import risk.engine.service.service.ICounterMetricService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/14 16:55
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/metric/counter")
public class CounterMetricController {

    @Resource
    private ICounterMetricService counterMetricService;

    @GetMapping("/list")
    public ResponseVO list(@ModelAttribute CounterMetricParam param) {
        log.info("List CounterMetric: {}", param);
        return ResponseVO.success(counterMetricService.list(param));
    }

    @PostMapping("/insert")
    public ResponseVO insert(@RequestBody CounterMetricParam param) {
        log.info("Insert CounterMetric: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(param.getIncidentCode())
                || StringUtils.isEmpty(param.getIncidentCode())
                || StringUtils.isEmpty(param.getMetricCode())
                || StringUtils.isEmpty(param.getMetricName())
                || StringUtils.isEmpty(param.getWindowSize())
                || StringUtils.isEmpty(param.getAggregationType())
                || Objects.isNull(param.getStatus())
                || CollectionUtils.isEmpty(param.getAttributeKey())
                || Objects.isNull(param.getMetricType())
        );
        return ResponseVO.success(counterMetricService.insert(param));
    }

    @PostMapping("/delete")
    public ResponseVO delete(@RequestBody CounterMetricParam param) {
        log.info("Delete CounterMetric: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(param.getId()));
        return ResponseVO.success(counterMetricService.delete(param.getId()));
    }

    @PostMapping("/update")
    public ResponseVO update(@RequestBody CounterMetricParam param) {
        log.info("Update CounterMetric: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(param.getId())
                        || StringUtils.isEmpty(param.getMetricName())
                        || StringUtils.isEmpty(param.getWindowSize())
                        || StringUtils.isEmpty(param.getAggregationType())
                        || Objects.isNull(param.getStatus())
                );
        return ResponseVO.success(counterMetricService.updateByPrimaryKey(param));
    }

    @GetMapping("/detail")
    public ResponseVO detail(@RequestParam("id") Long id) {
        log.info("Detail CounterMetric: {}", id);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(Objects.isNull(id));
        return ResponseVO.success(counterMetricService.getOne(id));
    }

}
