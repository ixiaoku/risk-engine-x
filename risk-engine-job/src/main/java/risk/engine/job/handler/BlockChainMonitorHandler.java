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

    @XxlJob("marketNoticeNewJob")
    public void executeMarketNoticeNew() {
        String param = XxlJobHelper.getJobParam(); // 获取任务参数
        XxlJobHelper.log("binanceNoticeJob, param: " + param);
        log.info("binanceNoticeJob job executed successfully!");
    }


}
