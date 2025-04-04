package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.MetricMapper;
import risk.engine.db.entity.Metric;
import risk.engine.service.service.IMetricService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:55
 * @Version: 1.0
 */
@Service
public class MetricServiceImpl implements IMetricService {

    @Resource
    private MetricMapper metricMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return metricMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(Metric record) {
        return metricMapper.insert(record) > 0;
    }

    @Override
    public Metric selectByPrimaryKey(Long id) {
        return metricMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(Metric record) {
        return metricMapper.updateByPrimaryKey(record) > 0;
    }

    @Override
    public List<Metric> selectByExample(Metric record) {
        return metricMapper.selectByExample(record);
    }
}
