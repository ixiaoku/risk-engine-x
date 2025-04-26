package risk.engine.job.task;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.components.kafka.RiskKafkaProducer;
import risk.engine.db.entity.QueueFailedMessagePO;
import risk.engine.service.service.IAlarmRecordService;
import risk.engine.service.service.IQueueFailedMessageService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/4/26 16:30
 * @Version: 1.0
 */
@Component
public class QueueFailedTask {

    @Resource
    private IQueueFailedMessageService failedMessageService;

    @Resource
    private RiskKafkaProducer kafkaProducer;

    @Resource
    private IAlarmRecordService alarmRecordService;

    public void execute() {
        List<QueueFailedMessagePO> messagePOList = failedMessageService.selectByStatus(0);
        if (CollectionUtils.isEmpty(messagePOList)) return;
        for (QueueFailedMessagePO message : messagePOList) {
            final QueueFailedMessagePO currentMessage = message;
            if (Objects.equals(message.getMaxRetry(), message.getRetry())) {
                //达到最大重试次数 触发lark告警 可以根据情况替换成电话告警
                alarmRecordService.insertAsync("kafka消费异常，topic：" + message.getTopic(), message.getErrorMessage());
                // 发送成功，更新状态为终态
                message.setStatus(2);
                message.setUpdateTime(LocalDateTime.now());
                failedMessageService.updateById(message);
                continue;
            }
            kafkaProducer
                    .sendMessageCallback(currentMessage.getTopic(), currentMessage.getMessage())
                    .addCallback(
                            success -> {
                                // 发送成功，更新状态为成功
                                currentMessage.setStatus(1);
                                currentMessage.setUpdateTime(LocalDateTime.now());
                                failedMessageService.updateById(currentMessage);
                            },
                            failure -> {
                                // 发送失败，增加retry次数
                                currentMessage.setRetry(currentMessage.getRetry() + 1);
                                currentMessage.setUpdateTime(LocalDateTime.now());
                                failedMessageService.updateById(currentMessage);
                            }
                    );
        }
    }
}
