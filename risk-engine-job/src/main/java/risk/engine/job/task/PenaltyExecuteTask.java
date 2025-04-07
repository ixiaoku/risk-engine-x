package risk.engine.job.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.ApplicationContextUtil;
import risk.engine.db.entity.PenaltyRecordPO;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.service.handler.IPenaltyHandler;
import risk.engine.service.service.IPenaltyRecordService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: X
 * @Date: 2025/3/17 00:20
 * @Version: 1.0
 */
@Slf4j
@Component
public class PenaltyExecuteTask {

    @Resource
    private IPenaltyRecordService penaltyRecordService;

    @Resource
    private ApplicationContextUtil applicationContextUtil;

    public void execute() {

        log.info("PenaltyExecuteTask start");
        PenaltyRecordPO record = new PenaltyRecordPO();
        record.setRetry(3);
        record.setStatus(PenaltyStatusEnum.WAIT.getCode());
        List<PenaltyRecordPO> penaltyRecordList = penaltyRecordService.selectExample(record);
        if (CollectionUtils.isEmpty(penaltyRecordList)) {
            return;
        }
        AtomicReference<Integer> retry = new AtomicReference<>(0);
        penaltyRecordList.forEach(penaltyRecord -> {
            IPenaltyHandler penaltyHandler = (IPenaltyHandler) applicationContextUtil.getBeanByClassName(penaltyRecord.getPenaltyDef());
            PenaltyStatusEnum penaltyStatusEnum = penaltyHandler.doPenalty(penaltyRecord);
            if (Objects.equals(penaltyStatusEnum.getCode(), PenaltyStatusEnum.WAIT.getCode())) {
               retry.getAndSet(retry.get() + 1);
               penaltyRecord.setRetry(penaltyRecord.getRetry());
            }
            penaltyRecord.setStatus(penaltyStatusEnum.getCode());
            penaltyRecord.setPenaltyResult("调用成功");
            penaltyRecord.setUpdateTime(LocalDateTime.now());
            penaltyRecordService.updateByPrimaryKey(penaltyRecord);
        });
        log.info("PenaltyExecuteTask success");
    }
}
