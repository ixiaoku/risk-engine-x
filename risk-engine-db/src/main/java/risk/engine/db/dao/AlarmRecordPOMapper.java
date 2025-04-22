package risk.engine.db.dao;

import risk.engine.db.entity.AlarmRecordPO;

import java.util.List;

public interface AlarmRecordPOMapper {

    int insert(AlarmRecordPO record);

    List<AlarmRecordPO> selectByExample(AlarmRecordPO example);

    AlarmRecordPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(AlarmRecordPO record);
}