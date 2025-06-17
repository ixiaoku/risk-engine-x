package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import risk.engine.client.feign.CounterMetricFeignClient;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.PageResult;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.CounterMetricParam;
import risk.engine.dto.vo.CounterMetricVO;
import risk.engine.service.service.ICounterMetricService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 计数器指标
 * @Author: X
 * @Date: 2025/3/14 16:55
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/metric/counter")
public class CounterMetricFeignController implements CounterMetricFeignClient {

    @Resource
    private ICounterMetricService counterMetricService;

    @PostMapping("/list")
    @Override
    public PageResult<CounterMetricVO> list(@RequestBody CounterMetricParam param) {
        log.info("List CounterMetric: {}", param);
        return counterMetricService.list(param);
    }

    @PostMapping("/insert")
    @Override
    public Boolean insert(@RequestBody CounterMetricParam param) {
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
        return counterMetricService.insert(param);
    }

    @PostMapping("/delete")
    @Override
    public Boolean delete(@RequestBody CounterMetricParam param) {
        log.info("Delete CounterMetric: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(param.getId()));
        return counterMetricService.delete(param.getId());
    }

    @PostMapping("/update")
    @Override
    public Boolean update(@RequestBody CounterMetricParam param) {
        log.info("Update CounterMetric: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(param.getId())
                        || StringUtils.isEmpty(param.getMetricName())
                        || StringUtils.isEmpty(param.getWindowSize())
                        || StringUtils.isEmpty(param.getAggregationType())
                        || Objects.isNull(param.getStatus())
                );
        return counterMetricService.updateByPrimaryKey(param);
    }

    @GetMapping("/detail")
    @Override
    public CounterMetricVO getOne(@RequestParam("id") Long id) {
        log.info("Detail CounterMetric: {}", id);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(Objects.isNull(id));
        return counterMetricService.getOne(id);
    }

}
