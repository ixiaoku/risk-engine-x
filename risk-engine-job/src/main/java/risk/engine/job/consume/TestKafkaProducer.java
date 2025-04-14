package risk.engine.job.consume;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: X
 * @Date: 2025/4/12 15:01
 * @Version: 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendTestMessage() {
        JSONObject message = new JSONObject();
        message.put("table", "incident");
        message.put("data", new JSONObject() {{
            put("id", "123");
            put("name", "测试数据");
        }});

        kafkaTemplate.send("risk_binlog_topic", message.toJSONString());
        log.info("Kafka 测试消息发送成功: {}", message);
    }
}
