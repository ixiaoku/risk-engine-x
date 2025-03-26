package risk.engine.common.util;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPInputStream;

/**
 * @Author: X
 * @Date: 2025/3/26 19:15
 * @Version: 1.0
 */
public class HttpProxyUtil {
    private static final int TIMEOUT = 5000;
    private static final int MAX_RETRY = 3; // 失败重试次数

    // **线程安全** 的代理池
    private static final List<HttpHost> PROXY_LIST = new CopyOnWriteArrayList<>(Arrays.asList(
            new HttpHost("115.231.181.40", 8128),
            new HttpHost("211.83.1.98", 18000),
            new HttpHost("47.123.7.220", 9100),
            new HttpHost("211.83.1.115", 18000)
    ));

    // 连接池管理
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();

    static {
        CONNECTION_MANAGER.setMaxTotal(50); // 最大连接数
        CONNECTION_MANAGER.setDefaultMaxPerRoute(10); // 单路由最大连接数
    }

    /**
     * 发送 GET 请求（带失败重试）
     */
    public static String sendGet(String url) {
        return sendRequest(url, null, false);
    }

    /**
     * 发送 POST 请求（带失败重试）
     */
    public static String sendPost(String url, String jsonPayload) {
        return sendRequest(url, jsonPayload, true);
    }

    /**
     * 核心请求方法（支持 GET/POST）
     */
    private static String sendRequest(String url, String jsonPayload, boolean isPost) {
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            HttpHost proxy = getRandomProxy();
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(CONNECTION_MANAGER)
                    .build()) {
                HttpResponse response = isPost ? executePost(httpClient, url, jsonPayload, proxy)
                        : executeGet(httpClient, url, proxy);
                return handleResponse(response);
            } catch (Exception e) {
                System.err.println("请求失败，代理：" + proxy + "，错误：" + e.getMessage());
                if (attempt == MAX_RETRY - 1) {
                    return null; // 最后一次失败，返回 null
                }
            }
        }
        return null;
    }

    /**
     * 执行 GET 请求
     */
    private static HttpResponse executeGet(CloseableHttpClient httpClient, String url, HttpHost proxy) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setConfig(buildRequestConfig(proxy));
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        request.setHeader("Accept-Encoding", "gzip");
        return httpClient.execute(request);
    }

    /**
     * 执行 POST 请求
     */
    private static HttpResponse executePost(CloseableHttpClient httpClient, String url, String jsonPayload, HttpHost proxy) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setConfig(buildRequestConfig(proxy));
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        request.setHeader("Accept-Encoding", "gzip");
        if (jsonPayload != null && !jsonPayload.isEmpty()) {
            request.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
            request.setHeader("Content-Type", "application/json");
        }
        return httpClient.execute(request);
    }

    /**
     * 解析响应，自动处理 GZIP
     */
    private static String handleResponse(HttpResponse response) throws IOException {
        byte[] rawBytes = EntityUtils.toByteArray(response.getEntity());
        // 获取 Content-Encoding
        String contentEncoding = response.getFirstHeader("Content-Encoding") != null
                ? response.getFirstHeader("Content-Encoding").getValue()
                : "";
        // 判断是否为 GZIP 压缩
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            return decompressGzip(rawBytes);
        } else {
            return new String(rawBytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * GZIP 解压缩
     */
    private static String decompressGzip(byte[] compressedData) throws IOException {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData))) {
            return new String(gzipInputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * 构造请求配置（代理 + 超时）
     */
    private static RequestConfig buildRequestConfig(HttpHost proxy) {
        return RequestConfig.custom()
                .setProxy(proxy)
                .setConnectTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();
    }

    /**
     * 随机获取代理 IP（支持动态更新）
     */
    private static HttpHost getRandomProxy() {
        if (PROXY_LIST.isEmpty()) {
            throw new RuntimeException("代理池为空");
        }
        return PROXY_LIST.get(new Random().nextInt(PROXY_LIST.size()));
    }

    /**
     * 允许动态更新代理池（外部调用）
     */
    public static void updateProxyList(List<HttpHost> newProxies) {
        PROXY_LIST.clear();
        PROXY_LIST.addAll(newProxies);
    }

}
