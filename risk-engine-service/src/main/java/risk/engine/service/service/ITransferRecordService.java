package risk.engine.service.service;

import risk.engine.db.entity.TransferRecord;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 02:02
 * @Version: 1.0
 */
public interface ITransferRecordService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(TransferRecord record);

    TransferRecord selectByPrimaryKey(Long id);

    List<TransferRecord> selectByExample(TransferRecord transferRecord);

    boolean updateByPrimaryKey(TransferRecord record);

}
