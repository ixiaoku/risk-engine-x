package risk.engine.db.dao;

import risk.engine.db.entity.TransferRecord;

public interface TransferRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TransferRecord record);

    TransferRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKey(TransferRecord record);
}