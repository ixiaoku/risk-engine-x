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
import risk.engine.common.util.CryptoUtils;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.crawler.monitor.ICrawlerBlockChainHandler;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.constant.BlockChainConstant;
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
 * @Date: 2025/3/9 22:45
 * @Version: 1.0
 */
@Slf4j
@Component
public class EthereumFetcherHandler implements ICrawlerBlockChainHandler {

    @Resource
    private ICrawlerTaskService crawlerTaskService;
    @Resource
    private RedisUtil redisUtil;

    private Web3j web3j;
    private static final int CONFIRMATION_BLOCKS = 6; // ETH确认块数
    private static final String LAST_BLOCK_KEY = "eth:lastBlock";
    private static final String TX_SET_KEY = "eth:processedTxs";
    private static final Integer INDEX = 1;//100太大了 接口容易被限频 改成0
    private static final BigDecimal ETH_THRESHOLD = new BigDecimal("0.05"); // 金额过滤

    @Override
    public void start() throws IOException {
        String secretKey = CryptoUtils.getDesSecretKey();
        String key = CryptoUtils.desDecrypt(BlockChainConstant.ETH_DATA_KEY, secretKey);
        web3j = Web3j.build(new HttpService(BlockChainConstant.INFURA_URL + key));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this::pollBlocks);
    }

    private void pollBlocks() {
        try {
            BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger confirmedBlock = latestBlock.subtract(BigInteger.valueOf(CONFIRMATION_BLOCKS));
            String lastBlockStr = (String) redisUtil.get(LAST_BLOCK_KEY);
            BigInteger lastBlock = StringUtils.isEmpty(lastBlockStr) ? confirmedBlock.subtract(BigInteger.valueOf(INDEX)) : new BigInteger(lastBlockStr);
            for (BigInteger i = lastBlock.add(BigInteger.ONE); i.compareTo(confirmedBlock) <= 0; i = i.add(BigInteger.ONE)) {
                EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(i), true).send();
                EthBlock.Block block = ethBlock.getBlock();
                List<CrawlerTaskPO> crawlerTasks = getCrawlerTaskList(block, i);
                if (CollectionUtils.isNotEmpty(crawlerTasks)) {
                    crawlerTaskService.batchInsert(crawlerTasks);
                    log.info("ETH Block {} processed, {} transactions saved", i, crawlerTasks.size());
                }
                redisUtil.set(LAST_BLOCK_KEY, i.toString());
            }
        } catch (Exception e) {
            log.error("ETH polling error: {}", e.getMessage());
        }
    }

    private List<CrawlerTaskPO> getCrawlerTaskList(EthBlock.Block block, BigInteger blockNumber) throws IOException {
        List<CrawlerTaskPO> crawlerTasks = new ArrayList<>();
        if (CollectionUtils.isEmpty(block.getTransactions())) return crawlerTasks;

        for (EthBlock.TransactionResult<?> txResult : block.getTransactions()) {
            Transaction tx = (Transaction) txResult.get();
            String txHash = tx.getHash();
            // 去重检查
            if (redisUtil.sismember(TX_SET_KEY, txHash)) {
                continue;
            }
            BigDecimal valueInEther = Convert.fromWei(tx.getValue().toString(), Convert.Unit.ETHER);
            // 过滤小于0.05 ETH的交易
            if (valueInEther.compareTo(ETH_THRESHOLD) <= 0) {
                continue;
            }
            String from = tx.getFrom();
            String to = tx.getTo();
            BigInteger gasUsed = getTransactionGasUsed(txHash);
            BigInteger gasPrice = tx.getGasPrice();
            BigDecimal gasFee = Convert.fromWei(gasUsed.multiply(gasPrice).toString(), Convert.Unit.ETHER);
            ChainTransferDTO dto = new ChainTransferDTO();
            dto.setSendAddress(from);
            dto.setReceiveAddress(to);
            dto.setAmount(valueInEther);
            dto.setUAmount(BigDecimal.ZERO);
            dto.setHash(txHash);
            dto.setHeight(blockNumber.intValue());
            dto.setChain("Ethereum");
            dto.setToken("ETH");
            dto.setFee(gasFee);
            dto.setTransferTime(block.getTimestamp().longValue());
            //公告
            String createdAt = DateTimeUtil.getTimeByTimestamp(dto.getTransferTime() * 1000);
            String content = String.format(CrawlerConstant.ADDRESS_BOT_TITLE, dto.getChain(), dto.getSendAddress(), dto.getReceiveAddress(), dto.getAmount());
            AnnouncementDTO announcementDTO = new AnnouncementDTO(CrawlerConstant.OVER_TRANSFER_TITLE, content, createdAt);
            dto.setAnnouncement(announcementDTO);

            CrawlerTaskPO task = crawlerTaskService.getCrawlerTask(txHash, IncidentCodeEnum.TRANSFER_CHAIN.getCode(), JSON.toJSONString(dto));
            crawlerTasks.add(task);
            redisUtil.sadd(TX_SET_KEY, txHash); // 记录已处理交易
        }
        return crawlerTasks;
    }


    private BigInteger getTransactionGasUsed(String txHash) throws IOException {
        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
        return receipt.getTransactionReceipt().get().getGasUsed();
    }
}