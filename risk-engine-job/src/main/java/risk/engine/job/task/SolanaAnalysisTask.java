package risk.engine.job.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import risk.engine.crawler.monitor.transfer.SolanaBlockFetcherHandler;
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
public class SolanaAnalysisTask {

    @Resource
    private SolanaBlockFetcherHandler fetcherHandler;

    @Resource
    private ITransferRecordService transferRecordService;

    public void execute() {
        log.info("Quartz 定时抓取Solana链上数据...");
        crawlerSolBlock();
    }

    private void crawlerSolBlock() {
        try {
            List<ChainTransferDTO> chainTransferDTOList = fetcherHandler.getTransactions();
            if (CollectionUtils.isEmpty(chainTransferDTOList)) {
                return;
            }
            log.info("SolanaAnalysisTask 一分钟一次定时抓取Solana 链上数据 size: {}", chainTransferDTOList.size());
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
                transferRecordService.insert(transferRecord);
            });
        } catch (IOException e) {
            log.error("错误信息：{}" ,e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
