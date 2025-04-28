package risk.engine.db.dao;

import risk.engine.db.entity.CounterMetricPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/29 00:16
 * @Version: 1.0
 */
public interface CounterMetricMapper {

    int insert(CounterMetricPO record);

    List<CounterMetricPO> selectByExample(CounterMetricPO example);

    int updateByPrimaryKey(CounterMetricPO record);

}
