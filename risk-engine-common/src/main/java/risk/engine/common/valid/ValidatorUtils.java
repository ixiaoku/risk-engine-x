package risk.engine.common.valid;

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
    public static ValidatorException<String> EmptyThrowException() {
        return value -> {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("Input string must not be null or empty");
            }
        };
    }
}

