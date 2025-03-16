package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.PenaltyRecordMapper;
import risk.engine.db.entity.Penalty;
import risk.engine.db.entity.PenaltyRecord;
import risk.engine.service.service.IPenaltyRecordService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 12:59
 * @Version: 1.0
 */
@Service
public class PenaltyRecordServiceImpl implements IPenaltyRecordService {

    @Resource
    private PenaltyRecordMapper penaltyRecordMapper;


    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return penaltyRecordMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(PenaltyRecord record) {
        return penaltyRecordMapper.insert(record) > 0;
    }

    @Override
    public Penalty selectByPrimaryKey(Long id) {
        return penaltyRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(PenaltyRecord record) {
        return penaltyRecordMapper.updateByPrimaryKey(record) > 0;
    }
}
