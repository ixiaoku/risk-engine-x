package risk.engine.job.config;

/**
 * @Author: X
 * @Date: 2025/3/16 00:51
 * @Version: 1.0
 */

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import risk.engine.job.task.BitcoinAnalysisTask;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail fetchBlockchainJobDetail() {
        return JobBuilder.newJob(BitcoinAnalysisTask.class)
                .withIdentity("fetchBlockchainJob")  // 任务名称
                .storeDurably()  // 任务存储到数据库
                .build();
    }

    @Bean
    public Trigger fetchBlockchainJobTrigger(JobDetail fetchBlockchainJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(fetchBlockchainJobDetail)
                .withIdentity("fetchBlockchainTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))  // 每 1 分钟执行一次
                .build();
    }
}
