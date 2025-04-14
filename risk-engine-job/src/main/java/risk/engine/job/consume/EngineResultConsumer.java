package risk.engine.job.consume;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.service.handler.RiskEngineExecutorHandler;

import javax.annotation.Resource;

@Slf4j
@Component
@RocketMQMessageListener(topic = "engine_result_topic", consumerGroup = "consumer-group-engine-save-db")
public class EngineResultConsumer implements RocketMQListener<String> {

    @Resource
    private RiskEngineExecutorHandler riskEngineExecutorHandler;

    @Override
    public void onMessage(String message) {
        try {
            RiskExecuteEngineDTO riskExecuteEngineDTO = new Gson().fromJson(message, RiskExecuteEngineDTO.class);
            riskEngineExecutorHandler.saveEngineResult(riskExecuteEngineDTO);
            log.info("Consume success EngineResult saved to rocketmq");
        } catch (Exception e) {
            log.error("mq消息失败 错误信息：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}