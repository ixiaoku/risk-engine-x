package risk.engine.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Slf4j
public class OkHttpUtil {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    /**
     * GET 请求（带参数）
     */
    public static String get(String url, Map<String, String> params) {
        try {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            if (params != null) {
                params.forEach(urlBuilder::addQueryParameter);
            }
            Request request = new Request.Builder().url(urlBuilder.build()).get().build();
            return execute(request);
        } catch (Exception e) {
            log.error("GET 请求失败，url={}，params={}", url, params, e);
            throw new RuntimeException("GET 请求异常", e);
        }
    }

    /**
     * GET 请求（无参数）
     */
    public static String get(String url) {
        try {
            Request request = new Request.Builder().url(url).get().build();
            return execute(request);
        } catch (Exception e) {
            log.error("GET 请求失败，url={}", url, e);
            throw new RuntimeException("GET 请求异常", e);
        }
    }

    /**
     * POST 请求（application/json）
     */
    public static String postJson(String url, String jsonBody) {
        try {
            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder().url(url).post(body).build();
            return execute(request);
        } catch (Exception e) {
            log.error("POST JSON 请求失败，url={}，body={}", url, jsonBody, e);
            throw new RuntimeException("POST JSON 请求异常", e);
        }
    }

    /**
     * POST 请求（application/x-www-form-urlencoded）
     */
    public static String postForm(String url, Map<String, String> formParams) {
        try {
            FormBody.Builder formBuilder = new FormBody.Builder();
            if (formParams != null) {
                formParams.forEach(formBuilder::add);
            }
            Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
            return execute(request);
        } catch (Exception e) {
            log.error("POST 表单请求失败，url={}，params={}", url, formParams, e);
            throw new RuntimeException("POST 表单请求异常", e);
        }
    }

    /**
     * 执行请求，统一处理响应和异常
     */
    private static String execute(Request request) throws Exception {
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP 请求失败，状态码：" + response.code());
            }
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 模拟浏览器 GET 请求（支持 GZIP）
     */
    public static String spiderGet(String urlStr) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                log.error("模拟请求失败，响应码: {}", code);
                return null;
            }

            String encoding = connection.getHeaderField("Content-Encoding");
            BufferedReader reader = "gzip".equalsIgnoreCase(encoding)
                    ? new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())))
                    : new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            log.error("模拟浏览器请求失败，url={}", urlStr, e);
            throw new RuntimeException("模拟请求异常", e);
        }
    }
}