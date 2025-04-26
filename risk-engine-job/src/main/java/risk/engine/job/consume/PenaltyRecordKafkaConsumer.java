package risk.engine.job.consume;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import risk.engine.common.util.GsonUtil;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.service.handler.RiskEngineExecutorHandler;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class PenaltyRecordKafkaConsumer {

    @Resource
    private RiskEngineExecutorHandler riskEngineExecutorHandler;

    @KafkaListener(topics = "#{'${customer.kafka.topic}'}", groupId = "consume_group_penalty_record")
    public void handleEngineMessage(List<ConsumerRecord<String, String>> messageList) {
        try {
            for (ConsumerRecord<String, String> record : messageList) {
                log.info("接收 保存处置记录 Kafka消息，topic={}, partition={}, offset={}", record.topic(), record.partition(), record.offset());
                RiskExecuteEngineDTO dto = GsonUtil.fromJson(record.value(), RiskExecuteEngineDTO.class);
                riskEngineExecutorHandler.savePenalty(dto);
            }
        } catch (Exception e) {
            log.error("保存处置记录 kafka消息失败 错误信息：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}