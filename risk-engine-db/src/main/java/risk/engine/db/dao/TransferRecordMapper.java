package risk.engine.db.dao;

import risk.engine.db.entity.TransferRecord;

import java.util.List;

public interface TransferRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TransferRecord record);

    TransferRecord selectByPrimaryKey(Long id);

    List<TransferRecord> selectByExample(TransferRecord transferRecord);

    int updateByPrimaryKey(TransferRecord record);
}