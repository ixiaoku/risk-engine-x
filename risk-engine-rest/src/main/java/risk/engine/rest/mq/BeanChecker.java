package risk.engine.rest.mq;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Author: X
 * @Date: 2025/3/15 16:03
 * @Version: 1.0
 */
@Component
public class BeanChecker implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    public BeanChecker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        System.out.println("RocketMQTemplate in Spring: " + applicationContext.containsBean("rocketMQTemplate"));
    }
}
