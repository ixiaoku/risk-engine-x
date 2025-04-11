package risk.engine.common.function;

import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.exception.RiskException;

/**
 * @Author: X
 * @Date: 2025/3/12 21:04
 * @Version: 1.0
 */
public class ValidatorHandler {

    // 提供一个静态方法来创建校验器
    public static Validator<String> notEmpty() {
        return value -> value != null && !value.isEmpty();
    }

    // 提供一个静态方法来创建校验器
    public static ValidatorFunction verify(ErrorCodeEnum errorCodeEnum) {
        return value -> {
            if (value) {
                throw new RiskException(errorCodeEnum);
            }
        };
    }
}

