package risk.engine.job.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.service.service.ITransferRecordService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 00:44
 * @Version: 1.0
 */
@Slf4j
@Component
public class BitcoinAnalysisTask {

    @Resource
    private ITransferRecordService transactionTransferRecordService;

    public void execute() {
        log.info("Quartz 定时抓取Bitcoin链上数据...");
    }
}
