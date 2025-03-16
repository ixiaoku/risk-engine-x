package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.IndicatorMapper;
import risk.engine.db.entity.Indicator;
import risk.engine.service.service.IIndicatorService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 12:55
 * @Version: 1.0
 */
@Service
public class IndicatorServiceImpl implements IIndicatorService {

    @Resource
    private IndicatorMapper indicatorMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return indicatorMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(Indicator record) {
        return indicatorMapper.insert(record) > 0;
    }

    @Override
    public Indicator selectByPrimaryKey(Long id) {
        return indicatorMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(Indicator record) {
        return indicatorMapper.updateByPrimaryKey(record) > 0;
    }
}
