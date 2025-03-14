package risk.engine.crawler.monitor.transaction;

/**
 * @Author: X
 * @Date: 2025/3/9 22:45
 * @Version: 1.0
 */

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class EthereumTransactionFetcher {

    private static final String INFURA_URL = "https://mainnet.infura.io/v3/98f9c8a03d054a7aa9972559460db851";
    private static final Web3j web3j = Web3j.build(new HttpService(INFURA_URL));

    public static void main(String[] args) throws Exception {
        // 1. 获取最新区块高度
        BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        System.out.println("最新区块高度: " + latestBlockNumber);

        // 2. 通过区块高度获取区块信息
        EthBlock ethBlock = web3j.ethGetBlockByNumber(
                org.web3j.protocol.core.DefaultBlockParameter.valueOf(latestBlockNumber), true
        ).send();

        EthBlock.Block block = ethBlock.getBlock();
        System.out.println("\n==== 区块详细信息 ====");
        System.out.println("区块哈希: " + block.getHash());
        System.out.println("出块时间: " + block.getTimestamp());
        System.out.println("矿工地址: " + block.getMiner());
        System.out.println("区块大小: " + block.getSize() + " bytes");
        System.out.println("交易数量: " + block.getTransactions().size());

        // 3. 遍历区块中的交易信息
        System.out.println("\n==== 交易信息 ====");
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

            // 6. 输出交易信息
            System.out.println("交易哈希: " + transaction.getHash());
            System.out.println("发送方: " + from);
            System.out.println("接收方: " + to);
            System.out.println("转账金额: " + valueInEther + " ETH");
            System.out.println("手续费: " + gasFee + " ETH");
            System.out.println("-------------------------");

            // 7. 交易总结
            System.out.println("📌【交易总结】" + from + " 给 " + to + " 转账 " + valueInEther + " ETH，手续费：" + gasFee + " ETH\n");
        }
    }

    /**
     * 获取交易的 Gas 消耗量
     */
    private static BigInteger getTransactionGasUsed(String txHash) throws IOException {
        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
        return receipt.getTransactionReceipt().get().getGasUsed();
    }
}