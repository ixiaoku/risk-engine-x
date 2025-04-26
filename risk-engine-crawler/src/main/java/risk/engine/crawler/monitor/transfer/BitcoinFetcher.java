package risk.engine.crawler.monitor.transfer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import risk.engine.common.util.GsonUtil;
import risk.engine.dto.dto.block.ChainTransferDTO;
import risk.engine.service.service.IBlockchainService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * 实时监控 BTC 链上已确认交易信息（基于 Blockstream.info API）
 * @Author: X
 * @Date: 2025/3/9 22:43
 * @Version: 1.0
 */
@Log
@Component
public class BitcoinFetcher {
    @Resource
    private IBlockchainService blockchainBlockService;
    private static final String BTC_API = "https://blockstream.info/api";
    private static final OkHttpClient client = new OkHttpClient();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void start() {
        try {
            // 获取最新区块高度
            String height = get(BTC_API + "/blocks/tip/height");

            // 获取区块哈希
            String blockHash = get(BTC_API + "/block-height/" + height);

            // 获取区块中的交易
            String gsonArray = get(BTC_API + "/block/" + blockHash + "/txs");
            JsonArray txs = GsonUtil.fromJson(gsonArray, JsonArray.class);
            if (!txs.isEmpty()) {
                System.out.println("该区块中的交易笔数" + txs.size());
                for (int i = 0; i < txs.size(); i++) {
                    JsonObject tx = txs.get(i).getAsJsonObject();
                    parseTransaction(tx);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * HTTP GET 请求
     */
    private String get(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
            throw new RuntimeException("响应为空");
        }
    }

    /**
     * 解析并打印交易详细信息
     */
    private ChainTransferDTO parseTransaction(JsonObject tx) {

        ChainTransferDTO chainTransferDTO = new ChainTransferDTO();

//        chainTransferDTO.setReceiveAddress();
//        chainTransferDTO.setAmount();
//        chainTransferDTO.setUAmount();
//        chainTransferDTO.setHash(tx.get("txid").getAsString());
//        chainTransferDTO.setHeight(tx.get("status").getAsJsonObject().get("block_height").getAsInt());
//        chainTransferDTO.setChain();
//        chainTransferDTO.setToken();
//        chainTransferDTO.setFee();
//        chainTransferDTO.setTransferTime(tx.get("locktime").getAsLong());
//        chainTransferDTO.setCreatedTime(LocalDateTime.now());
//        chainTransferDTO.setStatus(0);


        JsonArray vin = tx.getAsJsonArray("vin");
        BigDecimal totalInputBtc = BigDecimal.ZERO;
        for (int i = 0; i < vin.size(); i++) {
            JsonObject input = vin.get(i).getAsJsonObject();
            if (input.has("prevout") && !input.get("prevout").isJsonNull()) {
                chainTransferDTO.setSendAddress(input.has("prevout") ? input.get("prevout").getAsJsonObject().get("scriptpubkey_address").getAsString() : "未知");

                BigDecimal inputAmount = input.get("prevout").getAsJsonObject().get("value").getAsBigDecimal();
                totalInputBtc = totalInputBtc.add(inputAmount);
            }
        }
        // 输出详情
        JsonArray vout = tx.getAsJsonArray("vout");
        BigDecimal totalOutputBtc = BigDecimal.ZERO;
        for (int i = 0; i < vout.size(); i++) {
            JsonObject output = vout.get(i).getAsJsonObject();
            System.out.println("      - 输出索引: " + i);
            System.out.println("        输出金额: " + (output.get("value").getAsLong() / 100000000.0) + " BTC");
            System.out.println("        目标地址: " + output.get("scriptpubkey_address"));
            System.out.println("        脚本公钥: " + output.get("scriptpubkey"));
            System.out.println("        脚本类型: " + output.get("scriptpubkey_type"));
            BigDecimal outputAmount = output.get("value").getAsBigDecimal();
            totalOutputBtc = totalOutputBtc.add(outputAmount);
        }
        System.out.println("    输出总金额: " + totalOutputBtc + " BTC");

        // 矿工费
        BigDecimal feeBtc = totalInputBtc.subtract(totalOutputBtc);
        System.out.println("矿工费:" + feeBtc + " 聪BTC");


        return chainTransferDTO;
    }
}