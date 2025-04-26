package risk.engine.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;

public class GsonUtil {

    private static final Gson gson = new GsonBuilder()
            .serializeNulls() // 保留空字段
            .disableHtmlEscaping() // 不转义HTML标签
            .create();

    // 对象转 JSON 字符串
    public static <T> String toJson(T obj) {
        return gson.toJson(obj);
    }

    // JSON 字符串转对象
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // JSON 转 List<T>
    public static <T> List<T> fromJsonToList(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

}
