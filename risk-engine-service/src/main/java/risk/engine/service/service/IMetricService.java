package risk.engine.service.service;

import risk.engine.db.entity.Metric;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:55
 * @Version: 1.0
 */
public interface IMetricService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(Metric record);

    Metric selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(Metric record);

    List<Metric> selectByExample(Metric record);

}
