package risk.engine.crawler.monitor.transfer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.crawler.monitor.ICrawlerBlockChainHandler;
import risk.engine.dto.dto.block.ChainTransferDTO;
import risk.engine.dto.dto.block.SolanaBlockDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/14 23:29
 * @Version: 1.0
 */
@Slf4j
@Component
public class SolBlockFetcherHandler implements ICrawlerBlockChainHandler {

    private static final String RPC_URL = "https://api.devnet.solana.com";

    private static final String SLOT_JSON = "{\"jsonrpc\":\"2.0\",\"id\":1, \"method\":\"getSlot\"}";

    private static final Gson gson = new Gson();

    public List<ChainTransferDTO> getTransactions() throws IOException {
        //抓取最新slot
        long latestSlot = getLatestSlot();
        if (latestSlot == -1) {
            return null;
        }
        log.info("Latest Block Slot: {}", latestSlot);
        //抓取最新区块信息
        JsonObject blockData = getBlockDetails(latestSlot);
        if (blockData == null) {
            return null;
        }
        return printTransactions(blockData);
    }

    // 获取最新区块高度
    private long getLatestSlot() throws IOException {
        String result = OkHttpUtil.post(RPC_URL, SLOT_JSON);
        if (StringUtils.isEmpty(result)) {
            log.error("Get latest block slot failed");
            return -1;
        }
        JsonObject jsonResponse = JsonParser.parseString(result).getAsJsonObject();
        return jsonResponse.get("result").getAsLong();
    }

    // 通过区块高度获取区块详细信息
    private JsonObject getBlockDetails(long slot) throws IOException {
        String jsonRequest = "{ \"jsonrpc\": \"2.0\", \"id\": 1, \"method\": \"getBlock\", " +
                "\"params\": [" + slot + ", { \"encoding\": \"json\", \"transactionDetails\": \"full\", \"rewards\": false }] }";
        String result = OkHttpUtil.post(RPC_URL, jsonRequest);
        if (StringUtils.isEmpty(result)) {
            log.error("Get block details failed");
            return null;
        }
        return JsonParser.parseString(result).getAsJsonObject().getAsJsonObject("result");
    }

    /** 提取交易信息
     * 	1.	手续费（Fee）
     * 	•	直接从 meta.fee 获取。
     * 	2.	获取转账信息（Sender & Receiver）
     * 	•	transaction.message.accountKeys 里存储交易涉及的账户。
     * 	•	transaction.message.instructions 里的 accounts 字段存储了执行交易的账户索引：
     * 	•	accounts[0] 通常是接收者（to）。
     * 	•	accounts[1] 通常是发送者（from）。
     * 	•	通过 accountKeys[索引] 找到具体的地址。
     * 	3.	获取交易金额（Amount）
     * 	•	通过 preBalances（交易前余额） 和 postBalances（交易后余额） 计算出资金变动情况。
     * 	4.	获取代币类型（Token）
     * 	•	如果 preTokenBalances 和 postTokenBalances 为空，则交易涉及的是 SOL（Lamports）。
     * 	•	如果 postTokenBalances 不为空，则涉及 SPL 代币，币种可以从 mint 字段获取。
     * @param blockData 区块信息
     */

    private List<ChainTransferDTO> printTransactions(JsonObject blockData) {
        List<ChainTransferDTO> transferDTOList = new ArrayList<>();
        SolanaBlockDTO solanaBlockDTO = gson.fromJson(blockData, SolanaBlockDTO.class);
        JsonArray jsonArray = blockData.getAsJsonArray("transactions");
        jsonArray.forEach(tx -> {
            JsonObject root = (JsonObject) tx;
            // 解析meta数据
            JsonObject meta = root.getAsJsonObject("meta");
            // 手续费
            long fee = meta.get("fee").getAsLong();
            // 获取交易前后余额
            JsonArray preBalances = meta.getAsJsonArray("preBalances");
            JsonArray postBalances = meta.getAsJsonArray("postBalances");
            // 解析账户信息
            JsonObject transaction = root.getAsJsonObject("transaction");
            JsonObject message = transaction.getAsJsonObject("message");
            JsonArray accountKeys = message.getAsJsonArray("accountKeys");
            JsonArray instructions = message.getAsJsonArray("instructions");
            // 获取交易签名
            JsonArray signatures = transaction.getAsJsonArray("signatures");
            // 解析 from 和 to
            String from = "";
            String to = "";
            long amount = 0;
            String token = "SOL";  // 默认 SOL
            if (!instructions.isEmpty()) {
                JsonObject firstInstruction = instructions.get(0).getAsJsonObject();
                JsonArray accounts = firstInstruction.getAsJsonArray("accounts");
                if (accounts.size() >= 2) {
                    from = accountKeys.get(accounts.get(1).getAsInt()).getAsString();
                    to = accountKeys.get(accounts.get(0).getAsInt()).getAsString();
                }
            }
            // 计算金额
            if (preBalances.size() >= 2 && postBalances.size() >= 2) {
                long senderPreBalance = preBalances.get(1).getAsLong();
                long senderPostBalance = postBalances.get(1).getAsLong();
                amount = senderPreBalance - senderPostBalance;  // 计算转账金额
            }
            // 解析代币（如果涉及 SPL 代币）
            JsonArray postTokenBalances = meta.getAsJsonArray("postTokenBalances");
            if (!postTokenBalances.isEmpty()) {
                JsonObject tokenBalance = postTokenBalances.get(0).getAsJsonObject();
                token = tokenBalance.get("mint").getAsString();  // 获取代币合约地址
            }
            ChainTransferDTO chainTransferDTO = new ChainTransferDTO();
            chainTransferDTO.setSendAddress(from);
            chainTransferDTO.setReceiveAddress(to);
            chainTransferDTO.setAmount(new BigDecimal(amount).divide(new BigDecimal("1000000000.0"), 2));
            chainTransferDTO.setUAmount(new BigDecimal("0"));
            chainTransferDTO.setHash(signatures.get(0).getAsString());
            chainTransferDTO.setHeight(solanaBlockDTO != null ? solanaBlockDTO.getBlockHeight().intValue() : 0);
            chainTransferDTO.setChain("Solana");
            chainTransferDTO.setToken("SOL");
            chainTransferDTO.setFee(new BigDecimal(fee).divide(new BigDecimal("1000000000.0"), 2));
            chainTransferDTO.setTransferTime(0L);
            chainTransferDTO.setCreatedTime(LocalDateTime.now());
            chainTransferDTO.setStatus(0);
            transferDTOList.add(chainTransferDTO);
        });
        return transferDTOList;
    }
}