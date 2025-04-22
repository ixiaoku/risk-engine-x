package risk.engine.service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import risk.engine.db.dao.AlarmRecordPOMapper;
import risk.engine.db.entity.AlarmRecordPO;
import risk.engine.service.service.IAlarmRecordService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/22 21:39
 * @Version: 1.0
 */
@Slf4j
@Service
public class AlarmRecordServiceImpl implements IAlarmRecordService {

    @Resource
    private AlarmRecordPOMapper alarmRecordPOMapper;

    @Value("${spring.application.name}")
    private String serveName;

    @Value("${spring.profiles.active}")
    private String environment;

    @Override
    public boolean insert(AlarmRecordPO alarmRecord) {
        return alarmRecordPOMapper.insert(alarmRecord) > 0;
    }

    @Override
    public boolean update(AlarmRecordPO alarmRecord) {
        return alarmRecordPOMapper.updateByPrimaryKey(alarmRecord) > 0;
    }

    @Override
    public void insertAsync(String message, String stack) {
        AlarmRecordPO alarmRecordPO = new AlarmRecordPO();
        alarmRecordPO.setEnv(environment);
        alarmRecordPO.setUrl("https://open.larksuite.com/open-apis/bot/v2/hook/a8dbd31a-9c38-4960-8b5f-adba3766e2e5");
        alarmRecordPO.setServeName(serveName);
        alarmRecordPO.setAlarmType("Exception");
        alarmRecordPO.setAlarmLevel("error");
        alarmRecordPO.setStatus(2);
        alarmRecordPO.setRetry(0);
        alarmRecordPO.setChannel("lark");
        alarmRecordPO.setMessage(message);
        alarmRecordPO.setStack(stack);
        alarmRecordPO.setExtraData(null);
        alarmRecordPO.setCreateTime(LocalDateTime.now());
        alarmRecordPO.setUpdateTime(LocalDateTime.now());
        int size = alarmRecordPOMapper.insert(alarmRecordPO);
        System.out.println("Alarm record size: " + size);
//        CompletableFuture
//                .runAsync(() -> alarmRecordPOMapper.insert(alarmRecordPO))
//                .exceptionally(ex -> {
//                    log.error("保存告警记录 异步任务失败, 异常: {}", ex.getMessage(), ex);
//                    return null;
//                });
    }

    @Override
    public List<AlarmRecordPO> selectByExample(AlarmRecordPO example) {
        return alarmRecordPOMapper.selectByExample(example);
    }

}
