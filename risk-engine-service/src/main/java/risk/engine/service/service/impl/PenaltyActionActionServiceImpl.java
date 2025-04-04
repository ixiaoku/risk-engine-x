package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.PenaltyActionMapper;
import risk.engine.db.entity.PenaltyActionPO;
import risk.engine.service.service.IPenaltyActionService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
@Service
public class PenaltyActionActionServiceImpl implements IPenaltyActionService {

    @Resource
    private PenaltyActionMapper penaltyActionMapper;


    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return penaltyActionMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(PenaltyActionPO record) {
        return penaltyActionMapper.insert(record) > 0;
    }

    @Override
    public PenaltyActionPO selectByPrimaryKey(Long id) {
        return penaltyActionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PenaltyActionPO> selectByExample(PenaltyActionPO penalty) {
        return penaltyActionMapper.selectByExample(penalty);
    }

    @Override
    public boolean updateByPrimaryKey(PenaltyActionPO record) {
        return penaltyActionMapper.updateByPrimaryKey(record) > 0;
    }
}
