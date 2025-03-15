package risk.engine.job.config;

/**
 * @Author: X
 * @Date: 2025/3/16 00:51
 * @Version: 1.0
 */
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class QuartzStartupRunner {

    @Bean
    public CommandLineRunner scheduleJob(Scheduler scheduler) {
        return args -> {
            try {
                scheduler.start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        };
    }
}
