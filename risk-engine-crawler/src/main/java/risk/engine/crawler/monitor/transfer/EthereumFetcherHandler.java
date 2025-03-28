package risk.engine.crawler.monitor.transfer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import risk.engine.common.util.CryptoUtils;
import risk.engine.crawler.monitor.ICrawlerBlockChainHandler;
import risk.engine.dto.constant.BlockChainConstant;
import risk.engine.dto.dto.block.ChainTransferDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/9 22:45
 * @Version: 1.0
 */
@Slf4j
@Component
public class EthereumFetcherHandler implements ICrawlerBlockChainHandler {

    private Web3j web3j = null;

    @Override
    public List<ChainTransferDTO> getTransactions() throws IOException {

        String secretKey = CryptoUtils.getDesSecretKey();
        String key = CryptoUtils.desDecrypt(BlockChainConstant.ETH_DATA_KEY, secretKey);
        web3j = Web3j.build(new HttpService(BlockChainConstant.INFURA_URL + key));
        // 1. 获取最新区块高度
        BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        log.info("Ethereum链 最新区块高度: {}", latestBlockNumber);

        // 2. 通过区块高度获取区块信息
        EthBlock ethBlock = web3j
                .ethGetBlockByNumber(org.web3j.protocol.core.DefaultBlockParameter.valueOf(latestBlockNumber), true)
                .send();

        EthBlock.Block block = ethBlock.getBlock();

        // 3. 遍历区块中的交易信息
        List<ChainTransferDTO> transactions = new ArrayList<>();
        for (TransactionResult<?> txResult : block.getTransactions()) {
            Transaction transaction = (Transaction) txResult.get();
            // 4. 解析交易详情
            String from = transaction.getFrom();
            String to = transaction.getTo();
            BigDecimal valueInEther = Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER);

            // 5. 获取交易手续费
            BigInteger gasUsed = getTransactionGasUsed(transaction.getHash());
            BigInteger gasPrice = transaction.getGasPrice();
            BigDecimal gasFee = Convert.fromWei(gasUsed.multiply(gasPrice).toString(), Convert.Unit.ETHER);

            ChainTransferDTO chainTransferDTO = new ChainTransferDTO();
            chainTransferDTO.setSendAddress(from);
            chainTransferDTO.setReceiveAddress(to);
            chainTransferDTO.setAmount(valueInEther);
            chainTransferDTO.setUAmount(BigDecimal.ZERO);
            chainTransferDTO.setHash(transaction.getHash());
            chainTransferDTO.setHeight(latestBlockNumber.intValue());
            chainTransferDTO.setChain("Ethereum");
            chainTransferDTO.setToken("ETH");
            chainTransferDTO.setFee(gasFee);
            chainTransferDTO.setTransferTime(block.getTimestamp().longValue());
            chainTransferDTO.setCreatedTime(LocalDateTime.now());
            chainTransferDTO.setStatus(0);
            transactions.add(chainTransferDTO);
        }
        return transactions;
    }
    /**
     * 获取交易的 Gas 消耗量
     */
    private BigInteger getTransactionGasUsed(String txHash) throws IOException {
        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
        return receipt.getTransactionReceipt().get().getGasUsed();
    }

}