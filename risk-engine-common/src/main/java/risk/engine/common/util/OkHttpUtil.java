package risk.engine.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @Author: X
 * @Date: 2025/3/14 23:53
 * @Version: 1.0
 */
@Slf4j
public class OkHttpUtil {

    private static final OkHttpClient client = new OkHttpClient();
    /**
     * 发送 GET 请求
     *
     * @param url    请求 URL
     * @param params 请求参数（Map格式，会拼接到 URL 后面）
     * @return 响应字符串
     */
    public static String get(String url, Map<String, String> params) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            params.forEach(urlBuilder::addQueryParameter);
        }
        Request request = new Request.Builder().url(urlBuilder.build()).get().build();

        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 发送 POST 请求（JSON 格式）
     *
     * @param url    请求 URL
     * @param json   JSON 字符串
     * @return 响应字符串
     */
    public static String post(String url, String json) {
        try {
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                return response.body() != null ? response.body().string() : null;
            }
        } catch (Exception e) {
            log.error("Http Post请求报错：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    /**
     * 发送 POST 请求（表单格式）
     *
     * @param url    请求 URL
     * @param params 表单参数
     * @return 响应字符串
     */
    public static String postForm(String url, Map<String, String> params) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null) {
            params.forEach(formBuilder::add);
        }
        RequestBody body = formBuilder.build();
        Request request = new Request.Builder().url(url).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 爬虫模拟浏览器使用的http get请求
     * @param urlStr
     * @return
     */
    public static String get(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate"); // 支持 GZIP
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 检查是否使用了 GZIP 压缩
                String contentEncoding = connection.getHeaderField("Content-Encoding");
                BufferedReader reader;
                if ("gzip".equalsIgnoreCase(contentEncoding)) {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();
                return response.toString();
            } else {
                log.error("HTTP 请求失败，响应码: {}", responseCode);
                connection.disconnect();
                return null;
            }
        } catch (IOException e) {
            log.error("HTTP 请求失败: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


}
