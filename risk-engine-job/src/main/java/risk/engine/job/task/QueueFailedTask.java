package risk.engine.job.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.redis.RedisUtil;
import risk.engine.components.kafka.RiskKafkaProducer;
import risk.engine.db.entity.QueueFailedMessagePO;
import risk.engine.dto.enums.QueueMessageStatusEnum;
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
@Slf4j
@Component
public class QueueFailedTask {

    @Resource
    private IQueueFailedMessageService failedMessageService;

    @Resource
    private RiskKafkaProducer kafkaProducer;

    @Resource
    private IAlarmRecordService alarmRecordService;

    @Resource
    private RedisUtil redisUtil;

    public void execute() {
        List<QueueFailedMessagePO> messagePOList = failedMessageService.selectByStatus(QueueMessageStatusEnum.FAIL.getCode());
        if (CollectionUtils.isEmpty(messagePOList)) return;
        for (QueueFailedMessagePO message : messagePOList) {
            final QueueFailedMessagePO currentMessage = message;
            if (Objects.equals(message.getMaxRetry(), message.getRetry())) {
                //达到最大重试次数 触发lark告警 可以根据情况替换成电话告警
                alarmRecordService.insertAsync("kafka消费异常，topic：" + message.getTopic(), message.getErrorMessage());
                continue;
            }
            kafkaProducer
                    .sendMessageCallback(currentMessage.getTopic(), currentMessage.getMessage())
                    .addCallback(
                            success -> {
                                // 发送成功之后，检查Redis幂等Key
                                String redisKey = currentMessage.getOriginalKey();
                                boolean exists = Objects.isNull(redisUtil.get(redisKey));
                                if (exists) {
                                    // Redis里没有了，说明消费成功了，才能改状态
                                    currentMessage.setStatus(QueueMessageStatusEnum.SUCCESS.getCode());
                                    log.info("补偿发送成功且消费成功，更新为成功状态，redisKey={}", redisKey);
                                } else {
                                    log.info("补偿发送成功，但消费还未成功，保留状态，redisKey={}", redisKey);
                                }
                                // 无论如何，重试次数 +1
                                currentMessage.setRetry(currentMessage.getRetry() + 1);
                                currentMessage.setUpdateTime(LocalDateTime.now());
                                failedMessageService.updateById(currentMessage);
                            },
                            failure -> {
                                // 发送失败
                                log.error("补偿发送消息失败，topic={}, offset={}, error={}",
                                        currentMessage.getTopic(), currentMessage.getOffset(), failure.getMessage());
                                // 失败也要+1 retry
                                currentMessage.setRetry(currentMessage.getRetry() + 1);
                                currentMessage.setUpdateTime(LocalDateTime.now());
                                failedMessageService.updateById(currentMessage);
                            }
                    );
        }
    }
}
