package risk.engine.db.dao;

import risk.engine.db.entity.CounterMetricPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/29 00:16
 * @Version: 1.0
 */
public interface CounterMetricMapper {

    CounterMetricPO selectByPrimaryKey(Long id);

    List<CounterMetricPO> selectByExample(CounterMetricPO example);

    int insert(CounterMetricPO record);

    int updateByPrimaryKey(CounterMetricPO record);

    int deleteByPrimaryKey(Long id);

}
