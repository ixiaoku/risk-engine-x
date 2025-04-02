package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.job.task.EthereumAnalysisTask;
import risk.engine.job.task.SolanaAnalysisTask;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/19 10:50
 * @Version: 1.0
 */
@Slf4j
@Component
public class BlockChainMonitorHandler {

    @Resource
    private EthereumAnalysisTask ethereumAnalysisTask;

    @Resource
    private SolanaAnalysisTask solanaAnalysisTask;

    @XxlJob("bitcoinMonitorJob")
    public void executeBitcoinMonitor() {
        try {
            String param = XxlJobHelper.getJobParam();

            XxlJobHelper.log("bitcoinMonitorJob, param: " + param);
            log.info("bitcoinMonitorJob job executed successfully!");
        } catch (Exception e) {
            log.error("bitcoinMonitorJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("bitcoinMonitorJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @XxlJob("ethereumMonitorJob")
    public void executeEthereumMonitor() {
        try {
            String param = XxlJobHelper.getJobParam();
            ethereumAnalysisTask.execute();
            XxlJobHelper.log("ethereumMonitorJob, param: " + param);
            log.info("ethereumMonitorJob job executed successfully!");
        } catch (Exception e) {
            log.error("ethereumMonitorJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("ethereumMonitorJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @XxlJob("solMonitorJob")
    public void executeSolMonitor() {
        try {
            String param = XxlJobHelper.getJobParam();
            solanaAnalysisTask.execute();
            XxlJobHelper.log("solMonitorJob, param: " + param);
            log.info("solMonitorJob job executed successfully!");
        } catch (Exception e) {
            log.error("solMonitorJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("solMonitorJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
