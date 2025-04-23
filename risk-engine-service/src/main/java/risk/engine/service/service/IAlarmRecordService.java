package risk.engine.service.service;

import risk.engine.db.entity.AlarmRecordPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/22 21:39
 * @Version: 1.0
 */
public interface IAlarmRecordService {

    boolean insert(AlarmRecordPO alarmRecord);

    boolean update(AlarmRecordPO alarmRecord);

    void insertAsync(String title, String stack);

    List<AlarmRecordPO> selectByExample(AlarmRecordPO example);

}
