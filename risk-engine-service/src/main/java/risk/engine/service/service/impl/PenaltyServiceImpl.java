package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.PenaltyMapper;
import risk.engine.db.entity.Penalty;
import risk.engine.service.service.IPenaltyService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
@Service
public class PenaltyServiceImpl implements IPenaltyService {

    @Resource
    private PenaltyMapper penaltyMapper;


    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return penaltyMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(Penalty record) {
        return penaltyMapper.insert(record) > 0;
    }

    @Override
    public Penalty selectByPrimaryKey(Long id) {
        return penaltyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Penalty> selectByExample(Penalty penalty) {
        return penaltyMapper.selectByExample(penalty);
    }

    @Override
    public boolean updateByPrimaryKey(Penalty record) {
        return penaltyMapper.updateByPrimaryKey(record) > 0;
    }
}
