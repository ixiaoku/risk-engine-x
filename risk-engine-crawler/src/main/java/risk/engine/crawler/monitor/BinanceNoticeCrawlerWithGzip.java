package risk.engine.crawler.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;

/**
 * @Author: X
 * @Date: 2025/3/18 13:00
 * @Version: 1.0
 */
public class BinanceNoticeCrawlerWithGzip {
    private static final String URL = "https://www.binance.com/bapi/composite/v1/public/market/notice/get?page=1&rows=20";

    // 代理池（可定期更新）
    private static final List<HttpHost> PROXY_LIST = Arrays.asList(
            new HttpHost("185.199.229.156", 7492),
            new HttpHost("31.186.239.244", 8080),
            new HttpHost("103.161.118.88", 8080),
            new HttpHost("195.154.255.118", 8000)
    );

    public static void main(String[] args) {
        HttpHost proxy = getRandomProxy(); // 选择随机代理
        System.out.println("使用代理: " + proxy);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(URL);

            // 设置代理 & 超时
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .setConnectTimeout(5000)
                    .setSocketTimeout(5000)
                    .build();
            request.setConfig(config);

            // 伪装请求头，避免反爬
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
            request.setHeader("Accept-Encoding", "gzip"); // 让服务器返回 GZIP 数据

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                byte[] rawBytes = EntityUtils.toByteArray(response.getEntity()); // 获取 GZIP 数据
                String jsonResponse = decompressGzip(rawBytes); // 解压 GZIP

                // 解析 JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                // 解析 data.title
                JsonNode notices = rootNode.path("data").path("list");
                for (JsonNode notice : notices) {
                    String title = notice.path("title").asText();
                    if (title.contains("Binance Futures Will Launch")) {
                        System.out.println("匹配公告: " + title);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * GZIP 解压缩
     */
    private static String decompressGzip(byte[] compressedData) throws IOException {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData))) {
            return new String(gzipInputStream.readAllBytes(), "UTF-8");
        }
    }

    /**
     * 随机获取代理 IP
     */
    private static HttpHost getRandomProxy() {
        Random random = new Random();
        return PROXY_LIST.get(random.nextInt(PROXY_LIST.size()));
    }
}
