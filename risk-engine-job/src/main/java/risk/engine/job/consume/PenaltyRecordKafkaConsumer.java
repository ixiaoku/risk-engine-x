package risk.engine.job.consume;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import risk.engine.common.util.GsonUtil;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.service.handler.RiskEngineExecutorHandler;

import javax.annotation.Resource;

@Slf4j
@Component
public class PenaltyRecordKafkaConsumer {

    @Resource
    private RiskEngineExecutorHandler riskEngineExecutorHandler;

    @KafkaListener(topics = "#{'${customer.kafka.topic}'}", groupId = "consume_group_penalty_record")
    public void handleEngineMessage(String message) {
        try {
            RiskExecuteEngineDTO dto = GsonUtil.fromJson(message, RiskExecuteEngineDTO.class);
            riskEngineExecutorHandler.savePenalty(dto);
        } catch (Exception e) {
            log.error("kafka消息失败 错误信息：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}