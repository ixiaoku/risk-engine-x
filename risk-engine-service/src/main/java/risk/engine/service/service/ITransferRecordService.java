package risk.engine.service.service;

import risk.engine.db.entity.TransferRecordPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 02:02
 * @Version: 1.0
 */
public interface ITransferRecordService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(TransferRecordPO record);

    TransferRecordPO selectByPrimaryKey(Long id);

    List<TransferRecordPO> selectByExample(TransferRecordPO transferRecord);

    boolean updateByPrimaryKey(TransferRecordPO record);

}
