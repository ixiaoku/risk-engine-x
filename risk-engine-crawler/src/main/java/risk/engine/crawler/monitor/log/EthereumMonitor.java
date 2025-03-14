package risk.engine.crawler.monitor.log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.math.BigInteger;

/**
 * @Author: X
 * @Date: 2025/3/9 22:45
 * @Version: 1.0
 */
public class EthereumMonitor {
    private static final String ETH_RPC = "https://rpc.ankr.com/eth"; // Ankr 免费公共节点
    private final OkHttpClient client = new OkHttpClient();
    private String lastBlockNumber = "0";

    public void start() {
        while (true) {
            try {
                // 获取最新区块
                Request request = new Request.Builder()
                        .url(ETH_RPC)
                        .post(okhttp3.RequestBody.create("{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"latest\",true],\"id\":1}", okhttp3.MediaType.get("application/json")))
                        .build();
                Response response = client.newCall(request).execute();
                JsonObject block = JsonParser.parseString(response.body().string()).getAsJsonObject().getAsJsonObject("result");

                String blockNumber = block.get("number").getAsString();
                if (!blockNumber.equals(lastBlockNumber)) {
                    lastBlockNumber = blockNumber;
                    JsonObject txs = block.getAsJsonObject("transactions");
                    for (String txKey : txs.keySet()) {
                        JsonObject tx = txs.getAsJsonObject(txKey);
                        String hash = tx.get("hash").getAsString();
                        String from = tx.get("from").getAsString();
                        String to = tx.get("to").getAsString();
                        BigInteger value = new BigInteger(tx.get("value").getAsString().substring(2), 16);
                        double ethAmount = value.divide(BigInteger.valueOf(10).pow(18)).doubleValue();
                        System.out.println("Ethereum 新交易: " + hash + ", 从: " + from + ", 到: " + to + ", 金额: " + ethAmount + " ETH");
                    }
                }
                Thread.sleep(5000); // 每5秒检查一次
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
