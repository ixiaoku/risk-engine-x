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
@RocketMQMessageListener(topic = "engine_result_topic", consumerGroup = "consumer-group-engine-penalty")
public class PenaltyConsumer implements RocketMQListener<String> {

    @Resource
    private RiskEngineExecutorHandler riskEngineExecutorHandler;

    @Override
    public void onMessage(String message) {
        try {
            RiskExecuteEngineDTO riskExecuteEngineDTO = new Gson().fromJson(message, RiskExecuteEngineDTO.class);
            riskEngineExecutorHandler.savePenalty(riskExecuteEngineDTO);
            log.info("Consume success penalty saved to rocketmq");
        } catch (Exception e) {
            //应该捕捉进行处理 消费失败存入mysql 进行回放重试 不影响后续消息消费
            log.error("mq消息失败 错误信息：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}