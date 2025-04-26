package risk.engine.job.consume;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import risk.engine.common.redis.RedisUtil;
import risk.engine.common.util.GsonUtil;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.service.handler.RiskEngineExecutorHandler;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class EngineResultKafkaConsumer {

    @Resource
    private RiskEngineExecutorHandler riskEngineExecutorHandler;

    @Resource
    private RedisUtil redisUtil;

    @KafkaListener(topics = "#{'${customer.kafka.topic}'}", groupId = "consumer_group_engine_result")
    public void handleEngineMessage(List<ConsumerRecord<String, String>> messageList) {
        for (ConsumerRecord<String, String> record : messageList) {
            processRecord(record);
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        log.info("接收到 保存业务结果记录 Kafka消息，topic={}, partition={}, offset={}", record.topic(), record.partition(), record.offset());
        String originalKey = record.topic() + "-" + record.partition() + "-" + record.offset();
        try {
            RiskExecuteEngineDTO riskExecuteEngineDTO = GsonUtil.fromJson(record.value(), RiskExecuteEngineDTO.class);
            riskEngineExecutorHandler.saveEngineResult(riskExecuteEngineDTO);
            // 消费成功，清理redis标记
            if (StringUtils.isNotEmpty(riskExecuteEngineDTO.getOriginalKey())) {
                redisUtil.del(riskExecuteEngineDTO.getOriginalKey());
            }
        } catch (Exception e) {
            log.error("处理单条消息异常，topic={}, partition={}, offset={}, 错误={}",
                    record.topic(), record.partition(), record.offset(), e.getMessage(), e);
            Object object = redisUtil.get(originalKey);
            if (Objects.isNull(object)) {
                redisUtil.set(originalKey, "kafka:" + record.topic(), 3 * 24 * 60 * 60);
            }
            throw new RuntimeException(e);
        }
    }
}