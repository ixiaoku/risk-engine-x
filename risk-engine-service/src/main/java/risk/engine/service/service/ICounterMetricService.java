package risk.engine.service.service;

import risk.engine.dto.PageResult;
import risk.engine.dto.param.CounterMetricParam;
import risk.engine.dto.vo.CounterMetricVO;

/**
 * @Author: X
 * @Date: 2025/4/29 01:28
 * @Version: 1.0
 */
public interface ICounterMetricService {

    boolean insert(CounterMetricParam param);

    PageResult<CounterMetricVO> list(CounterMetricParam param);

    boolean updateByPrimaryKey(CounterMetricParam param);

    CounterMetricVO getOne(Long id);

    boolean delete(Long id);

}
