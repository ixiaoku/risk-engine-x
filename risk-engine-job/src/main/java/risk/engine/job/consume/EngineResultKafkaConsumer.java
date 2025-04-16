package risk.engine.job.consume;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.service.handler.RiskEngineExecutorHandler;

import javax.annotation.Resource;

@Slf4j
@Component
public class EngineResultKafkaConsumer {

    @Resource
    private RiskEngineExecutorHandler riskEngineExecutorHandler;

    @KafkaListener(topics = "engine_result_topic", groupId = "consumer_group_engine_result")
    public void handleCanalMessage(String message) {
        try {
            RiskExecuteEngineDTO riskExecuteEngineDTO = new Gson().fromJson(message, RiskExecuteEngineDTO.class);
            riskEngineExecutorHandler.saveEngineResult(riskExecuteEngineDTO);
            log.info("Consume success EngineResult saved to rocketmq");
        } catch (Exception e) {
            log.error("kafka消息失败 错误信息：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}