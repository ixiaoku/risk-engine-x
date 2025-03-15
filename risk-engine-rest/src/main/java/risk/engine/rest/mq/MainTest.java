package risk.engine.rest.mq;

/**
 * @Author: X
 * @Date: 2025/3/15 13:43
 * @Version: 1.0
 */
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MainTest implements CommandLineRunner {

    @Resource
    private RiskEngineProducer riskEngineProducer;

    @Override
    public void run(String... args) throws Exception {
        riskEngineProducer.sendMessage("test_topic1", "Hello RocketMQ!");
    }

}
