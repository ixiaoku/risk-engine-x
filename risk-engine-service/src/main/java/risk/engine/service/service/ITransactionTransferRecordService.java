package risk.engine.service.service;

import risk.engine.db.entity.TransactionTransferRecord;

/**
 * @Author: X
 * @Date: 2025/3/16 02:02
 * @Version: 1.0
 */
public interface ITransactionTransferRecordService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(TransactionTransferRecord record);

    TransactionTransferRecord selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(TransactionTransferRecord record);

}
