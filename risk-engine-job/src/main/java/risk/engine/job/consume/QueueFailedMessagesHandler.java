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

    /**
     * 	第一次消费失败时，基于 topic + partition + offset 生成一个唯一key，并将失败信息存储到MySQL，同时在Redis中标记。
     * 	后续如果需要重试（无论是Kafka自身的消息重试，还是通过XXL-JOB定时轮询触发的补偿机制）：
     * 	首先根据原始的 topic+partition+offset 查找Redis中是否存在对应的失败标记。
     * 	如果不存在（Redis没有记录），说明这是一次新的失败消息，应该以新的offset生成新的失败记录和新的key。
     * 	如果存在（Redis中有记录），说明这条消息是之前那次失败后的重试，应继续沿用第一次失败时生成的原始key，而不是用新的offset。
     * 	这样可以保证在Kafka自动重试（offset变化）或定时任务重发过程中，所有相关重试操作都归属于同一条失败记录，避免产生重复记录或遗漏更新。
     * @param record the first input argument
     * @param exception the second input argument
     */
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
