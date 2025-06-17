package risk.engine.dto.vo;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/19 20:11
 * @Version: 1.0
 */
@Data
public class ResponseResult<T> {

    private Integer code;
    private String message;
    private T data;

    // 全参构造
    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 无 data 的构造
    public ResponseResult(Integer code, String message) {
        this(code, message, null);
    }

    // 静态工厂：成功
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "请求成功", data);
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(200, "请求成功", null);
    }

    // 静态工厂：失败
    public static <T> ResponseResult<T> fail(Integer code, String message) {
        return new ResponseResult<>(code, message, null);
    }

    public static <T> ResponseResult<T> fail(String message) {
        return fail(500, message);
    }
}
