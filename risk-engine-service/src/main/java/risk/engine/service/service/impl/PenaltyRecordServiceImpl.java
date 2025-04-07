package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.PenaltyRecordMapper;
import risk.engine.db.entity.PenaltyRecordPO;
import risk.engine.service.service.IPenaltyRecordService;

import javax.annotation.Resource;
import java.util.List;

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
    public boolean batchInsert(List<PenaltyRecordPO> records) {
        return penaltyRecordMapper.batchInsert(records) > 0;
    }

    @Override
    public List<PenaltyRecordPO> selectExample(PenaltyRecordPO penaltyRecord) {
        return penaltyRecordMapper.selectExample(penaltyRecord);
    }

    @Override
    public boolean updateByPrimaryKey(PenaltyRecordPO record) {
        return penaltyRecordMapper.updateByPrimaryKey(record) > 0;
    }
}
