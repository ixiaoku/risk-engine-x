package risk.engine.job.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
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
@Component
public class EthereumAnalysisTask {

    @Resource
    private EthereumFetcherHandler ethereumFetcherHandler;

    @Resource
    private ITransferRecordService transferRecordService;

    private void execute() {
        log.info("Ethereum 定时抓取Ethereum链上数据...");
        try {
            List<ChainTransferDTO> chainTransferDTOList = ethereumFetcherHandler.getTransactions();
            if (CollectionUtils.isEmpty(chainTransferDTOList)) {
                return;
            }
            log.info("EthereumAnalysisTask 一分钟一次定时抓取Ethereum 链上数据 size: {}", chainTransferDTOList.size());
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
