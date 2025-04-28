package risk.engine.service.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.db.dao.CounterMetricMapper;
import risk.engine.db.entity.CounterMetricPO;
import risk.engine.dto.param.CounterMetricParam;
import risk.engine.dto.vo.CounterMetricVO;
import risk.engine.service.service.ICounterMetricService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/4/29 01:29
 * @Version: 1.0
 */
@Service
public class CounterMetricServiceImpl implements ICounterMetricService {

    @Resource
    private CounterMetricMapper counterMetricMapper;

    @Override
    public boolean insert(CounterMetricParam param) {
        CounterMetricPO counterMetricPO = new CounterMetricPO();
        counterMetricPO.setMetricCode(param.getMetricCode());
        counterMetricPO.setMetricName(param.getMetricName());
        counterMetricPO.setMetricType(param.getMetricType());
        counterMetricPO.setIncidentCode(param.getIncidentCode());
        counterMetricPO.setAttributeKey(param.getAttributeKey());
        counterMetricPO.setWindowSize(param.getWindowSize());
        counterMetricPO.setAggregationType(param.getAggregationType());
        counterMetricPO.setStatus(param.getStatus());
        counterMetricPO.setDescription(param.getDescription());
        counterMetricPO.setCreateTime(LocalDateTime.now());
        counterMetricPO.setUpdateTime(LocalDateTime.now());
        return counterMetricMapper.insert(counterMetricPO) > 0;
    }

    @Override
    public List<CounterMetricVO> list(CounterMetricParam param) {
        CounterMetricPO query = new CounterMetricPO();
        query.setIncidentCode(param.getIncidentCode());
        query.setMetricCode(param.getMetricCode());
        query.setMetricName(param.getMetricName());
        List<CounterMetricPO> counterMetricVOS = counterMetricMapper.selectByExample(query);
        if (CollectionUtils.isEmpty(counterMetricVOS)) return List.of();
        return counterMetricVOS.stream().map(this::convertVO).collect(Collectors.toList());
    }

    @Override
    public boolean updateByPrimaryKey(CounterMetricParam param) {
        CounterMetricPO counterMetric = new CounterMetricPO();
        counterMetric.setId(param.getId());
        counterMetric.setMetricName(param.getMetricName());
        counterMetric.setMetricType(param.getMetricType());
        counterMetric.setWindowSize(param.getWindowSize());
        counterMetric.setAggregationType(param.getAggregationType());
        counterMetric.setStatus(param.getStatus());
        counterMetric.setDescription(param.getDescription());
        counterMetric.setUpdateTime(LocalDateTime.now());
        return false;
    }

    @Override
    public CounterMetricVO getOne(Long id) {
        CounterMetricPO counterMetric = counterMetricMapper.selectByPrimaryKey(id);
        return convertVO(counterMetric);
    }

    @Override
    public boolean delete(Long id) {
        return counterMetricMapper.deleteByPrimaryKey(id) > 0;
    }

    private CounterMetricVO convertVO(CounterMetricPO counterMetric) {
        CounterMetricVO counterMetricVO = new CounterMetricVO();
        counterMetricVO.setMetricCode(counterMetric.getMetricCode());
        counterMetricVO.setMetricName(counterMetric.getMetricName());
        counterMetricVO.setMetricType(counterMetric.getMetricType());
        counterMetricVO.setIncidentCode(counterMetric.getIncidentCode());
        counterMetricVO.setAttributeKey(counterMetric.getAttributeKey());
        counterMetricVO.setWindowSize(counterMetric.getWindowSize());
        counterMetricVO.setAggregationType(counterMetric.getAggregationType());
        counterMetricVO.setStatus(counterMetric.getStatus());
        counterMetricVO.setDescription(counterMetric.getDescription());
        counterMetricVO.setCreateTime(counterMetric.getCreateTime());
        counterMetricVO.setUpdateTime(counterMetric.getUpdateTime());
        return counterMetricVO;
    }

}
