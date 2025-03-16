package risk.engine.job.task;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import risk.engine.crawler.monitor.transfer.EthereumFetcherHandler;
import risk.engine.db.entity.TransferRecord;
import risk.engine.dto.dto.block.ChainTransferDTO;
import risk.engine.service.service.ITransferRecordService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 00:44
 * @Version: 1.0
 */
@Slf4j
public class EthereumAnalysisTask implements Job {

    @Resource
    private EthereumFetcherHandler ethereumFetcherHandler;

    @Resource
    private ITransferRecordService transactionTransferRecordService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Quartz 定时抓取BTC链上数据...");
        try {
            List<ChainTransferDTO> chainTransferDTOList = ethereumFetcherHandler.getTransactions();
            if (chainTransferDTOList == null || chainTransferDTOList.isEmpty()) {
                return;
            }
            chainTransferDTOList.forEach(chainTransferDTO -> {
                TransferRecord transferRecord = new TransferRecord();
                transferRecord.setSendAddress(chainTransferDTO.getSendAddress());
                transferRecord.setReceiveAddress(chainTransferDTO.getReceiveAddress());
                transferRecord.setAmount(chainTransferDTO.getAmount());
                transferRecord.setUAmount(chainTransferDTO.getUAmount());
                transferRecord.setHash(chainTransferDTO.getHash());
                transferRecord.setHeight(chainTransferDTO.getHeight());
                transferRecord.setChain(chainTransferDTO.getChain());
                transferRecord.setToken(chainTransferDTO.getToken());
                transferRecord.setFee(chainTransferDTO.getFee());
                transferRecord.setTransferTime(chainTransferDTO.getTransferTime());
                transferRecord.setCreatedTime(chainTransferDTO.getCreatedTime());
                transferRecord.setStatus(chainTransferDTO.getStatus());
                transactionTransferRecordService.insert(transferRecord);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
