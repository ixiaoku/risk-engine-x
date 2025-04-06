package risk.engine.crawler.monitor.transfer;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.redis.RedisUtil;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: X
 * @Date: 2025/3/14 23:29
 * @Version: 1.0
 */
@Slf4j
@Component
public class SolBlockFetcherHandler implements ICrawlerBlockChainHandler {

    @Resource
    private ICrawlerTaskService crawlerTaskService;
    @Resource
    private RedisUtil redisUtil;

    private static final String RPC_URL = "https://api.devnet.solana.com";
    private static final String SLOT_JSON = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getSlot\"}";
    private static final String LAST_SLOT_KEY = "sol:lastSlot";
    private static final String TX_SET_KEY = "sol:processedTxs";
    private static final Integer INDEX = 1;//100太大了 接口容易被限频 改成1
    private static final BigDecimal SOL_THRESHOLD = new BigDecimal("1"); // 金额过滤
    private static final int CONFIRMATION_SLOTS = 32; // Solana确认slot数

    @Override
    public void start() throws IOException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this::pollSlots); // 异步轮询
    }

    private void pollSlots() {
        try {
            long latestSlot = getLatestSlot();
            if (latestSlot == -1) return;
            long confirmedSlot = latestSlot - CONFIRMATION_SLOTS;
            String lastSlotStr = (String) redisUtil.get(LAST_SLOT_KEY);
            long lastSlot = lastSlotStr == null ? confirmedSlot - INDEX : Long.parseLong(lastSlotStr);
            for (long i = lastSlot + 1; i <= confirmedSlot; i++) {
                JsonObject blockData = getBlockDetails(i);
                if (blockData == null) continue;
                List<CrawlerTaskPO> crawlerTasks = getCrawlerTaskList(blockData, i);
                if (!crawlerTasks.isEmpty()) {
                    crawlerTaskService.batchInsert(crawlerTasks);
                    log.info("Solana Slot {} processed, {} transactions saved", i, crawlerTasks.size());
                }
                redisUtil.set(LAST_SLOT_KEY, String.valueOf(i));
            }
        } catch (Exception e) {
            log.error("Solana polling error: {}", e.getMessage());
        }
    }

    private long getLatestSlot() throws IOException {
        String result = OkHttpUtil.post(RPC_URL, SLOT_JSON);
        if (StringUtils.isEmpty(result)) return -1;
        return JsonParser.parseString(result).getAsJsonObject().get("result").getAsLong();
    }

    private JsonObject getBlockDetails(long slot) throws IOException {
        String jsonRequest = "{ \"jsonrpc\": \"2.0\", \"id\": 1, \"method\": \"getBlock\", " +
                "\"params\": [" + slot + ", { \"encoding\": \"json\", \"transactionDetails\": \"full\", \"rewards\": false }] }";
        String result = OkHttpUtil.post(RPC_URL, jsonRequest);
        if (StringUtils.isEmpty(result)) return null;
        return JsonParser.parseString(result).getAsJsonObject().getAsJsonObject("result");
    }

    private List<CrawlerTaskPO> getCrawlerTaskList(JsonObject blockData, long slot) {
        List<CrawlerTaskPO> crawlerTasks = new ArrayList<>();
        JsonArray jsonArray = blockData.getAsJsonArray("transactions");
        jsonArray.forEach(tx -> {
            JsonObject root = (JsonObject) tx;
            JsonObject meta = root.getAsJsonObject("meta");
            long fee = meta.get("fee").getAsLong();
            JsonArray preBalances = meta.getAsJsonArray("preBalances");
            JsonArray postBalances = meta.getAsJsonArray("postBalances");
            JsonObject transaction = root.getAsJsonObject("transaction");
            JsonObject message = transaction.getAsJsonObject("message");
            JsonArray accountKeys = message.getAsJsonArray("accountKeys");
            JsonArray instructions = message.getAsJsonArray("instructions");
            JsonArray signatures = transaction.getAsJsonArray("signatures");
            String txHash = signatures.get(0).getAsString();

            // 去重检查
            if (redisUtil.sismember(TX_SET_KEY, txHash)) return;

            String from = "", to = "";
            long amount = 0;
            if (!instructions.isEmpty() && instructions.get(0).getAsJsonObject().has("accounts")) {
                JsonArray accounts = instructions.get(0).getAsJsonObject().getAsJsonArray("accounts");
                if (accounts.size() >= 2) {
                    from = accountKeys.get(accounts.get(1).getAsInt()).getAsString();
                    to = accountKeys.get(accounts.get(0).getAsInt()).getAsString();
                }
            }
            if (preBalances.size() >= 2 && postBalances.size() >= 2) {
                amount = preBalances.get(1).getAsLong() - postBalances.get(1).getAsLong();
            }
            BigDecimal amountInSol = new BigDecimal(amount).divide(new BigDecimal("1000000000"), 2, RoundingMode.HALF_UP);
            if (amountInSol.compareTo(SOL_THRESHOLD) <= 0) return; // 过滤小于1 SOL的交易

            ChainTransferDTO dto = new ChainTransferDTO();
            dto.setSendAddress(from);
            dto.setReceiveAddress(to);
            dto.setAmount(amountInSol);
            dto.setUAmount(BigDecimal.ZERO);
            dto.setHash(txHash);
            dto.setHeight((int) slot);
            dto.setChain("Solana");
            dto.setToken("SOL");
            dto.setFee(new BigDecimal(fee).divide(new BigDecimal("1000000000"), 2, RoundingMode.HALF_UP));
            dto.setTransferTime(blockData.get("blockTime").getAsLong() * 1000);
            //公告
            String createdAt = DateTimeUtil.getTimeByTimestamp(dto.getTransferTime() * 1000);
            String content = String.format(CrawlerConstant.ADDRESS_BOT_TITLE, dto.getChain(), dto.getSendAddress(), dto.getReceiveAddress(), dto.getAmount());
            AnnouncementDTO announcementDTO = new AnnouncementDTO(CrawlerConstant.OVER_TRANSFER_TITLE, content, createdAt);
            dto.setAnnouncement(announcementDTO);

            CrawlerTaskPO task = crawlerTaskService.getCrawlerTask(txHash, IncidentCodeEnum.TRANSFER_CHAIN.getCode(), JSON.toJSONString(dto));
            crawlerTasks.add(task);
            redisUtil.sadd(TX_SET_KEY, txHash);
        });
        return crawlerTasks;
    }
}