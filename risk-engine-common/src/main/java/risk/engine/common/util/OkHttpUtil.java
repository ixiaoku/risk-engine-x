package risk.engine.common.util;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
/**
 * @Author: X
 * @Date: 2025/3/14 23:53
 * @Version: 1.0
 */
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
    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
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
}
