//package risk.engine.service.handler;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RocketMQMessageListener(topic = "test_topic1", consumerGroup = "consumer-group-test1")
//public class RiskEngineConsumer implements RocketMQListener<String> {
//
//    @Override
//    public void onMessage(String message) {
//        log.info("RiskEngineConsumer.onMessage: {}", message);
//    }
//
//}