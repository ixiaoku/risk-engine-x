package risk.engine.dto.result;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/19 20:11
 * @Version: 1.0
 */
@Data
public class ResponseResult {

    private Integer code;

    private String message;

    private Object data;

    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public ResponseResult(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseResult success() {
        return new ResponseResult(200, "请求成功");
    }

    public static ResponseResult success(Object data) {
        return new ResponseResult(200, "请求成功", data);
    }

}
