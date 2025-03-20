package risk.engine.common.function;

import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/12 21:04
 * @Version: 1.0
 */
public class ValidatorUtils {

    // 提供一个静态方法来创建校验器
    public static Validator<String> notEmpty() {
        return value -> value != null && !value.isEmpty();
    }

    // 提供一个静态方法来创建校验器
    public static ValidatorException<Object> EmptyThrowException() {
        return value -> {
            if (Objects.isNull(value)) {
                throw new IllegalArgumentException("Input string must not be null or empty");
            }
        };
    }
}

