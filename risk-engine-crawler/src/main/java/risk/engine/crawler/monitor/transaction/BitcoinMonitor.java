package risk.engine.crawler.monitor.transaction;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.BlockchainBlock;
import risk.engine.dto.dto.block.BlockchainBlockDTO;
import risk.engine.service.service.IBlockchainBlockService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 实时监控 BTC 链上已确认交易信息（基于 Blockstream.info API）
 * @Author: X
 * @Date: 2025/3/9 22:43
 * @Version: 1.0
 */
@Component
public class BitcoinMonitor {
    @Resource
    private IBlockchainBlockService blockchainBlockService;
    private static final String BTC_API = "https://blockstream.info/api";
    private static final OkHttpClient client = new OkHttpClient();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String lastBlockHash = "";
    int i = 0;
    public void start() {
        while (true) {
            try {
                // 获取最新区块高度
                String height = get(BTC_API + "/blocks/tip/height");

                // 获取区块哈希
                String blockHash = get(BTC_API + "/block-height/" + height);

                if (!blockHash.equals(lastBlockHash)) {
                    lastBlockHash = blockHash;
                    // 获取区块详情
                    String blockDetail = get(BTC_API + "/block/" + blockHash);
                    BlockchainBlockDTO blockDTO = new Gson().fromJson(blockDetail, BlockchainBlockDTO.class);
                    System.out.println("\n=== 新区块 ===");
                    System.out.println("区块高度: " + blockDTO.getHeight());
                    System.out.println("区块哈希: " + blockDTO.getId());
                    System.out.println("时间戳: " + dateFormat.format(new Date(blockDTO.getTimestamp() * 1000)));
                    System.out.println("交易数量: " + blockDTO.getTx_count());
                    System.out.println("区块大小: " + blockDTO.getSize() + " 字节");
                    System.out.println("区块重量: " + blockDTO.getWeight());
                    System.out.println("难度: " + blockDTO.getDifficulty());
                    System.out.println("前一区块哈希: " + blockDTO.getPreviousblockhash());

                    saveBlock(blockDTO);

                    // 获取区块中的交易
                    String gsonArray = get(BTC_API + "/block/" + blockHash + "/txs");
                    JsonArray txs = new Gson().fromJson(gsonArray, JsonArray.class);
                    if (!txs.isEmpty()) {
                        System.out.println("该区块中的交易笔数" + txs.size());
                        for (int i = 0; i < txs.size(); i++) {
                            JsonObject tx = txs.get(i).getAsJsonObject();
                            parseTransaction(tx);
                        }
                    }
                }
                i++;
                System.out.println("----------------> 第" + i + "次执行 --------------->");
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void saveBlock(BlockchainBlockDTO blockDTO) {
        BlockchainBlock block = new BlockchainBlock();
        block.setHash(blockDTO.getId());
        block.setHeight(blockDTO.getHeight());
        block.setVersion(blockDTO.getVersion());
        block.setTimestamp(blockDTO.getTimestamp());
        block.setTxCount(blockDTO.getTx_count());
        block.setSize(blockDTO.getSize());
        block.setWeight(blockDTO.getWeight());
        block.setMerkleRoot(blockDTO.getMerkle_root());
        block.setPreviousBlockHash(blockDTO.getPreviousblockhash());
        block.setMedianTime(blockDTO.getMediantime());
        block.setNonce(blockDTO.getNonce());
        block.setBits(blockDTO.getBits());
        block.setDifficulty(blockDTO.getDifficulty());
        block.setCoin("BTC");
        block.setChain("Bitcoin");
        block.setCreateTime(LocalDateTime.now());
        boolean flag = blockchainBlockService.insert(block);
        System.out.println("保存成功 " + flag);
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
    private void parseTransaction(JsonObject tx) {
        System.out.println("\n  === 交易 ===");
        System.out.println("    交易哈希 (txid): " + tx.get("txid").getAsString());
        System.out.println("    版本号: " + tx.get("version").getAsInt());
        System.out.println("    锁定时间 (locktime): " + tx.get("locktime").getAsLong());
        System.out.println("    交易大小: " + tx.get("size").getAsInt() + " 字节");
        System.out.println("    交易重量: " + tx.get("weight").getAsInt());
        System.out.println("    确认状态: " + tx.get("status").getAsJsonObject().get("confirmed").getAsBoolean());
        System.out.println("    区块高度: " + tx.get("status").getAsJsonObject().get("block_height").getAsInt());
        System.out.println("    区块时间: " + dateFormat.format(new Date(tx.get("status").getAsJsonObject().get("block_time").getAsLong() * 1000)));

        // 输入详情
        System.out.println("    输入:");
        JsonArray vin = tx.getAsJsonArray("vin");
        BigDecimal totalInputBtc = BigDecimal.ZERO;
        for (int i = 0; i < vin.size(); i++) {
            JsonObject input = vin.get(i).getAsJsonObject();
            System.out.println("      - 来源交易哈希: " + input.get("txid").getAsString());
            System.out.println("        输出索引 (vout): " + input.get("vout").getAsInt());
            if (input.has("prevout") && !input.get("prevout").isJsonNull()) {
                System.out.println("        来源金额: " + (input.has("prevout") ? input.get("prevout").getAsJsonObject().get("value").getAsLong() / 100000000.0 : 0) + " BTC");
                System.out.println("        来源地址: " + (input.has("prevout") ? input.get("prevout").getAsJsonObject().get("scriptpubkey_address").getAsString() : "未知"));
                System.out.println("        脚本公钥: " + (input.has("prevout") ? input.get("prevout").getAsJsonObject().get("scriptpubkey").getAsString() : ""));
                System.out.println("        脚本类型: " + (input.has("prevout") ? input.get("prevout").getAsJsonObject().get("scriptpubkey_type").getAsString() : ""));
                BigDecimal inputAmount = input.get("prevout").getAsJsonObject().get("value").getAsBigDecimal();
                totalInputBtc = totalInputBtc.add(inputAmount);
            }
            System.out.println("        签名脚本: " + input.get("scriptSig"));
            System.out.println("        序列号: " + input.get("sequence"));
        }
        System.out.println("    输入总金额: " + totalInputBtc + " BTC");

        // 输出详情
        System.out.println("    输出:");
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
    }
}