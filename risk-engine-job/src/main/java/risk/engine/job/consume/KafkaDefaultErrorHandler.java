package risk.engine.job.consume;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/4/26 15:04
 * @Version: 1.0
 */
@Configuration
public class KafkaDefaultErrorHandler {

    @Resource
    private QueueFailedMessagesHandler failedMessagesHandler;

    @Bean
    public DefaultErrorHandler errorHandler() {
        // 每条消息最多重试3次，间隔2秒
        FixedBackOff backOff = new FixedBackOff(2000L, 3L);
        DefaultErrorHandler handler = new DefaultErrorHandler(failedMessagesHandler, backOff);
        // 只重试特定异常类型
        handler.addRetryableExceptions(RuntimeException.class);
        // 失败消息落库后是否提交offset
        handler.setCommitRecovered(true);
        return handler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.setBatchListener(true); // 支持批量
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

}
