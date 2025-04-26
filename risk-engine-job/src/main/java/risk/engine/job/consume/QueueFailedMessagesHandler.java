package risk.engine.job.consume;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.QueueFailedMessagePO;
import risk.engine.service.service.IQueueFailedMessageService;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Author: X
 * @Date: 2025/4/26 15:14
 * @Version: 1.0
 */
@Component
public class QueueFailedMessagesHandler implements ConsumerRecordRecoverer {

    @Resource
    private IQueueFailedMessageService failedMessageService;

    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception exception) {
        QueueFailedMessagePO failed = new QueueFailedMessagePO();
        failed.setTopic(record.topic());
        failed.setPartition(record.partition());
        failed.setOffset(record.offset());
        failed.setKey(record.key() != null ? record.key().toString() : null);
        failed.setMessage(record.value() != null ? record.value().toString() : null);
        failed.setErrorMessage(exception.getMessage());
        failed.setMaxRetry(10);
        failed.setRetry(0);
        failed.setStatus(0);
        failed.setType("kafka");
        failed.setCreateTime(LocalDateTime.now());
        failed.setUpdateTime(LocalDateTime.now());
        failedMessageService.insert(failed);
    }
}
