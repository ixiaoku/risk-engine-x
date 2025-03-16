package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.TransactionTransferRecordMapper;
import risk.engine.db.entity.TransactionTransferRecord;
import risk.engine.service.service.ITransactionTransferRecordService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 02:02
 * @Version: 1.0
 */
@Service
public class TransactionTransferRecordServiceImpl implements ITransactionTransferRecordService {

    @Resource
    private TransactionTransferRecordMapper transferRecordMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return transferRecordMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(TransactionTransferRecord record) {
        return transferRecordMapper.insert(record) > 0;
    }

    @Override
    public TransactionTransferRecord selectByPrimaryKey(Long id) {
        return transferRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(TransactionTransferRecord record) {
        return transferRecordMapper.deleteByPrimaryKey(record.getId()) > 0;
    }
}
