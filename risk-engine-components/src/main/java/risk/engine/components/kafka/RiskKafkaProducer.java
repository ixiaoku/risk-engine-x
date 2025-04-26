package risk.engine.components.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/4/12 15:01
 * @Version: 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskKafkaProducer {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public ListenableFuture<SendResult<String, String>> sendMessageCallback(String topic, String message) {
        return kafkaTemplate.send(topic, message);
    }
}
