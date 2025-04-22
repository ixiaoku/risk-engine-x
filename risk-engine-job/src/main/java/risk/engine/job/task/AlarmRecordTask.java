package risk.engine.job.task;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.AlarmRecordPO;
import risk.engine.service.handler.LarkHandler;
import risk.engine.service.service.IAlarmRecordService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/22 22:08
 * @Version: 1.0
 */
@Slf4j
@Component
public class AlarmRecordTask {

    @Resource
    private IAlarmRecordService alarmRecordService;

    @Resource
    private LarkHandler larkHandler;

    public void execute() {
        AlarmRecordPO recordQuery = new AlarmRecordPO();
        recordQuery.setStatus(2);
        recordQuery.setRetry(3);
        List<AlarmRecordPO> alarmRecordPOList = alarmRecordService.selectByExample(recordQuery);
        if (CollectionUtils.isEmpty(alarmRecordPOList)) return;
        Lists.partition(alarmRecordPOList, 200)
                .forEach(recordPOList -> {
                    for (AlarmRecordPO alarmRecord : recordPOList) {
                        boolean flag = larkHandler.sendMessage(alarmRecord.getMessage(), alarmRecord.getUrl());
                        if (flag) {
                            alarmRecord.setStatus(1);
                            alarmRecord.setUpdateTime(LocalDateTime.now());
                            alarmRecord.setSendTime(LocalDateTime.now());
                        } else {
                            alarmRecord.setStatus(0);
                            alarmRecord.setSendTime(LocalDateTime.now());
                            alarmRecord.setUpdateTime(LocalDateTime.now());
                            alarmRecord.setRetry(alarmRecord.getRetry() + 1);
                        }
                        alarmRecordService.update(alarmRecord);
                    }
                });
    }

}
