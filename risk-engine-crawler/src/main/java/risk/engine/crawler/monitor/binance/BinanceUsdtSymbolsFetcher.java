package risk.engine.crawler.monitor.binance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/15 12:13
 * @Version: 1.0
 */
@Component
public class BinanceUsdtSymbolsFetcher {

    private static final String BINANCE_EXCHANGE_INFO_URL = "https://api.binance.com/api/v3/exchangeInfo";

    public static List<String> getUsdtSymbols() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BINANCE_EXCHANGE_INFO_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Unexpected response: " + response);
            }

            String jsonData = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonData);
            JsonNode symbols = root.path("symbols");

            List<String> usdtSymbols = new ArrayList<>();
            for (JsonNode symbolNode : symbols) {
                String symbol = symbolNode.path("symbol").asText();
                String quoteAsset = symbolNode.path("quoteAsset").asText();
                String status = symbolNode.path("status").asText();
                // 只筛选出当前状态为 TRADING 并且 quoteAsset 是 USDT 的交易对
                if ("USDT".equals(quoteAsset) && "TRADING".equals(status)) {
                    usdtSymbols.add(symbol);
                }
            }
            return usdtSymbols;
        }
    }
}
