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
                .withIdentity("fetchBitcoinChainJob")
                .withIdentity("fetchEthChainJob")
                .withIdentity("fetchEthereumChainJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fetchBlockchainJobTrigger(JobDetail fetchBlockchainJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(fetchBlockchainJobDetail)
                .withIdentity("fetchBitcoinChainTrigger")
                .withIdentity("fetchEthereumChainTrigger")
                .withIdentity("fetchSolanaChainTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))  // 每 1 分钟执行一次
                .build();
    }
}
