package risk.engine.job.task;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import risk.engine.crawler.monitor.transfer.EthereumFetcherHandler;
import risk.engine.service.service.ITransferRecordService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 00:44
 * @Version: 1.0
 */
@Slf4j
public class BitcoinAnalysisTask implements Job {

    @Resource
    private EthereumFetcherHandler ethereumFetcherHandler;

    @Resource
    private ITransferRecordService transactionTransferRecordService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Quartz 定时抓取Bitcoin链上数据...");
        //EthereumAnalysisTask.c(ethereumFetcherHandler, transactionTransferRecordService);
    }
}
