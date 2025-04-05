package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.MetricMapper;
import risk.engine.db.entity.MetricPO;
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
    public boolean deleteByIncidentCode(String incidentCode) {
        return metricMapper.deleteByIncidentCode(incidentCode) > 0;
    }

    @Override
    public List<MetricPO> selectByExample(MetricPO record) {
        return metricMapper.selectByExample(record);
    }

    @Override
    public boolean batchInsert(List<MetricPO> record) {
        return metricMapper.batchInsert(record) > 0;
    }
}
