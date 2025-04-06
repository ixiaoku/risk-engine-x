package risk.engine.crawler.monitor.transfer;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import risk.engine.common.redis.RedisUtil;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.crawler.monitor.ICrawlerBlockChainHandler;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.block.ChainTransferDTO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: X
 * @Date: 2025/4/5 22:35
 * @Version: 1.0
 */
@Slf4j
@Component
public class BnbFetcherHandler implements ICrawlerBlockChainHandler {

    @Resource
    private ICrawlerTaskService crawlerTaskService;
    @Resource
    private RedisUtil redis;

    private Web3j web3j;
    private static final String BNB_RPC_URL = "https://bsc-dataseed.binance.org/";
    private static final int CONFIRMATION_BLOCKS = 15; // BNB确认块数
    private static final String LAST_BLOCK_KEY = "bnb:lastBlock";
    private static final String TX_SET_KEY = "bnb:processedTxs";
    private static final BigDecimal BNB_THRESHOLD = new BigDecimal("0.1"); // 示例金额过滤，可调整

    @Override
    public void start() throws IOException {
        web3j = Web3j.build(new HttpService(BNB_RPC_URL));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this::pollBlocks);
    }

    private void pollBlocks() {
        try {
            BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger confirmedBlock = latestBlock.subtract(BigInteger.valueOf(CONFIRMATION_BLOCKS));
            String lastBlockStr = (String) redis.get(LAST_BLOCK_KEY);
            BigInteger lastBlock = StringUtils.isEmpty(lastBlockStr) ? confirmedBlock.subtract(BigInteger.valueOf(100))
                    : new BigInteger(lastBlockStr);
            for (BigInteger i = lastBlock.add(BigInteger.ONE); i.compareTo(confirmedBlock) <= 0; i = i.add(BigInteger.ONE)) {
                EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(i), true).send();
                EthBlock.Block block = ethBlock.getBlock();
                List<CrawlerTaskPO> crawlerTasks = getCrawlerTaskList(block, i);
                if (!crawlerTasks.isEmpty()) {
                    crawlerTaskService.batchInsert(crawlerTasks);
                    log.info("BNB Block {} processed, {} transactions saved", i, crawlerTasks.size());
                }
                redis.set(LAST_BLOCK_KEY, i.toString());
            }
        } catch (Exception e) {
            log.error("BNB polling error: {}", e.getMessage());
        }
    }

    private List<CrawlerTaskPO> getCrawlerTaskList(EthBlock.Block block, BigInteger blockNumber) throws IOException {
        List<CrawlerTaskPO> crawlerTasks = new ArrayList<>();
        if (CollectionUtils.isEmpty(block.getTransactions())) return crawlerTasks;

        for (EthBlock.TransactionResult<?> txResult : block.getTransactions()) {
            Transaction tx = (Transaction) txResult.get();
            String txHash = tx.getHash();

            // 去重检查
            if (redis.sismember(TX_SET_KEY, txHash)) continue;

            BigDecimal valueInBnb = Convert.fromWei(tx.getValue().toString(), Convert.Unit.ETHER);
            if (valueInBnb.compareTo(BNB_THRESHOLD) <= 0) continue; // 过滤小于0.1 BNB的交易

            String from = tx.getFrom();
            String to = tx.getTo();
            BigInteger gasUsed = getTransactionGasUsed(txHash);
            BigInteger gasPrice = tx.getGasPrice();
            BigDecimal gasFee = Convert.fromWei(gasUsed.multiply(gasPrice).toString(), Convert.Unit.ETHER);

            ChainTransferDTO dto = new ChainTransferDTO();
            dto.setSendAddress(from);
            dto.setReceiveAddress(to);
            dto.setAmount(valueInBnb);
            dto.setUAmount(BigDecimal.ZERO);
            dto.setHash(txHash);
            dto.setHeight(blockNumber.intValue());
            dto.setChain("BNB Chain");
            dto.setToken("BNB");
            dto.setFee(gasFee);
            dto.setTransferTime(block.getTimestamp().longValue());
            //公告
            String createdAt = DateTimeUtil.getTimeByTimestamp(dto.getTransferTime() * 1000);
            String content = String.format(CrawlerConstant.ADDRESS_BOT_TITLE, dto.getChain(), dto.getSendAddress(), dto.getReceiveAddress(), dto.getAmount());
            AnnouncementDTO announcementDTO = new AnnouncementDTO(CrawlerConstant.OVER_TRANSFER_TITLE, content, createdAt);
            dto.setAnnouncement(announcementDTO);

            CrawlerTaskPO task = crawlerTaskService.getCrawlerTask(txHash, IncidentCodeEnum.TRANSFER_CHAIN.getCode(), JSON.toJSONString(dto));
            crawlerTasks.add(task);
            redis.sadd(TX_SET_KEY, txHash);
        }
        return crawlerTasks;
    }

    private BigInteger getTransactionGasUsed(String txHash) throws IOException {
        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
        return receipt.getTransactionReceipt().get().getGasUsed();
    }
}
