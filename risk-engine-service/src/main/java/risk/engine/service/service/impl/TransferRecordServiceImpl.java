package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.TransferRecordMapper;
import risk.engine.db.entity.TransferRecord;
import risk.engine.service.service.ITransferRecordService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 02:02
 * @Version: 1.0
 */
@Service
public class TransferRecordServiceImpl implements ITransferRecordService {

    @Resource
    private TransferRecordMapper transferRecordMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return transferRecordMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(TransferRecord record) {
        return transferRecordMapper.insert(record) > 0;
    }

    @Override
    public TransferRecord selectByPrimaryKey(Long id) {
        return transferRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(TransferRecord record) {
        return transferRecordMapper.deleteByPrimaryKey(record.getId()) > 0;
    }
}
