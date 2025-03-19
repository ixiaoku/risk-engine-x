package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.job.task.BitcoinAnalysisTask;
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
    private BitcoinAnalysisTask bitcoinAnalysisTask;

    @Resource
    private EthereumAnalysisTask ethereumAnalysisTask;

    @Resource
    private SolanaAnalysisTask solanaAnalysisTask;

    @XxlJob("bitcoinMonitorJob")
    public void executeBitcoinMonitor() {
        String param = XxlJobHelper.getJobParam();
        bitcoinAnalysisTask.execute();
        XxlJobHelper.log("bitcoinMonitorJob, param: " + param);
        log.info("bitcoinMonitorJob job executed successfully!");
    }

    @XxlJob("ethereumMonitorJob")
    public void executeEthereumMonitor() {
        String param = XxlJobHelper.getJobParam();
        ethereumAnalysisTask.execute();
        XxlJobHelper.log("ethereumMonitorJob, param: " + param);
        log.info("ethereumMonitorJob job executed successfully!");
    }

    @XxlJob("solMonitorJob")
    public void executeSolMonitor() {
        String param = XxlJobHelper.getJobParam();
        solanaAnalysisTask.execute();
        XxlJobHelper.log("solMonitorJob, param: " + param);
        log.info("solMonitorJob job executed successfully!");
    }


}
