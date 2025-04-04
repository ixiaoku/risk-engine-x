package risk.engine.db.dao;

import risk.engine.db.entity.TransferRecordPO;

import java.util.List;

public interface TransferRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TransferRecordPO record);

    TransferRecordPO selectByPrimaryKey(Long id);

    List<TransferRecordPO> selectByExample(TransferRecordPO transferRecord);

    int updateByPrimaryKey(TransferRecordPO record);
}