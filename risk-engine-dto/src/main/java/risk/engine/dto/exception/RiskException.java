package risk.engine.dto.exception;

import lombok.Getter;
import risk.engine.dto.enums.ErrorCodeEnum;

/**
 * @Author: X
 * @Date: 2025/4/11 20:36
 * @Version: 1.0
 */
@Getter
public class RiskException extends RuntimeException {

    private final int code;

    public RiskException(String message) {
        super(message);
        this.code = 500;
    }

    public RiskException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.code = errorCodeEnum.getCode();
    }

}
