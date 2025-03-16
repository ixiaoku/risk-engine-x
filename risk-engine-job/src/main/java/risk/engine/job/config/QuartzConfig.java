package risk.engine.job.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import risk.engine.job.task.BitcoinAnalysisTask;
import risk.engine.job.task.EthereumAnalysisTask;
import risk.engine.job.task.SolanaAnalysisTask;

/**
 * @Author: X
 * @Date: 2025/3/16 00:51
 * @Version: 1.0
 */
@Configuration
public class QuartzConfig {
    // BTC JobDetail 和 Trigger
    //@Bean
    public JobDetail fetchBitcoinJobDetail() {
        return JobBuilder.newJob(BitcoinAnalysisTask.class)
                .withIdentity("fetchBitcoinChainJob", "blockchain")
                .storeDurably()
                .build();
    }

    //@Bean
    public Trigger fetchBitcoinJobTrigger(JobDetail fetchBitcoinJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(fetchBitcoinJobDetail)
                .withIdentity("fetchBitcoinChainTrigger", "blockchain")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")) // 每分钟
                .build();
    }

    // ETH JobDetail 和 Trigger
    //@Bean
    public JobDetail fetchEthereumJobDetail() {
        return JobBuilder.newJob(EthereumAnalysisTask.class)
                .withIdentity("fetchEthereumChainJob", "blockchain")
                .storeDurably()
                .build();
    }

    //@Bean
    public Trigger fetchEthereumJobTrigger(JobDetail fetchEthereumJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(fetchEthereumJobDetail)
                .withIdentity("fetchEthereumChainTrigger", "blockchain")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))
                .build();
    }

    // SOL JobDetail 和 Trigger
    @Bean
    public JobDetail fetchSolanaJobDetail() {
        return JobBuilder.newJob(SolanaAnalysisTask.class)
                .withIdentity("fetchSolanaChainJob", "blockchain")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fetchSolanaJobTrigger(JobDetail fetchSolanaJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(fetchSolanaJobDetail)
                .withIdentity("fetchSolanaChainTrigger", "blockchain")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))
                .build();
    }
}
