package risk.engine.components.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: X
 * @Date: 2025/4/16 23:09
 * @Version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "customer.kafka")
@Data
public class KafkaConfig {
    private String topic;
}
