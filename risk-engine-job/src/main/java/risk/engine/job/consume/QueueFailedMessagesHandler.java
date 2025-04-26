package risk.engine.job.consume;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
        String originalKey = record.topic() + "-" + record.partition() + "-" + record.offset();
        //需要在消息体加上唯一id 消息重放的时候 可以找到这个消息
        String message = record.value() != null ? record.value().toString() : null;
        if (message != null) {
            JSONObject jsonObject = JSONObject.parseObject(message);
            String existOriginalKey = jsonObject.getString("originalKey");
            if (StringUtils.isEmpty(existOriginalKey)) {
                jsonObject.put("originalKey", originalKey);
                message = jsonObject.toJSONString(); // 补充后的
            } else {
                originalKey = existOriginalKey;
            }
        }
        failed.setOriginalKey(originalKey);
        failed.setMessage(message);
        failed.setErrorMessage(exception.getMessage());
        failed.setMaxRetry(10);
        failed.setRetry(0);
        failed.setStatus(0);
        failed.setType("kafka");
        failed.setCreateTime(LocalDateTime.now());
        failed.setUpdateTime(LocalDateTime.now());
        failedMessageService.insert(failed);
    }}
