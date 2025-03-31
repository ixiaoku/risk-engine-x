package risk.engine.service.handler.penalty;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.service.handler.RiskEngineHandler;

import javax.annotation.Resource;

@Slf4j
@Component
@RocketMQMessageListener(topic = "test_topic1", consumerGroup = "consumer-group-penalty")
public class PenaltyConsumer implements RocketMQListener<String> {

    @Resource
    private RiskEngineHandler riskEngineHandler;

    @Override
    public void onMessage(String message) {
        try {
            log.info("RiskEngineConsumer.onMessage: 消费成功");
            RiskExecuteEngineDTO riskExecuteEngineDTO = new Gson().fromJson(message, RiskExecuteEngineDTO.class);
            riskEngineHandler.savePenalty(riskExecuteEngineDTO);
        } catch (Exception e) {
            //应该捕捉进行处理 消费失败存入mysql 进行回放重试 不影响后续消息消费
            log.error("错误信息：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}