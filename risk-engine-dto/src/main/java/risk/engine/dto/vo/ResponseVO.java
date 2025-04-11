package risk.engine.dto.vo;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/19 20:11
 * @Version: 1.0
 */
@Data
public class ResponseVO {

    private Integer code;

    private String message;

    private Object data;

    public ResponseVO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public ResponseVO(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseVO success() {
        return new ResponseVO(200, "请求成功");
    }

    public static ResponseVO success(Object data) {
        return new ResponseVO(200, "请求成功", data);
    }

    public static ResponseVO fail(Integer code, String message) {
        return new ResponseVO(code, message);
    }

    public static ResponseVO fail(String message) {
        return fail(500, message);
    }

}
