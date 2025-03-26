package risk.engine.components.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RiskEngineProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, String messageText) {
        try {
            rocketMQTemplate.convertAndSend(topic, messageText);
            log.info("生产者 发送消息成功");
        } catch (Exception e) {
            log.error("生产者 错误信息：{}", e.getMessage(), e);
        }
    }
}