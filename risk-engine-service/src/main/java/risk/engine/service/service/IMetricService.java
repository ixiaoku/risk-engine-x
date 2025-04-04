package risk.engine.service.service;

import risk.engine.db.entity.MetricPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:55
 * @Version: 1.0
 */
public interface IMetricService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(MetricPO record);

    MetricPO selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(MetricPO record);

    List<MetricPO> selectByExample(MetricPO record);

}
