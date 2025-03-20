package risk.engine.common.function;

/**
 * @Author: X
 * @Date: 2025/3/12 21:03
 * @Version: 1.0
 */
@FunctionalInterface
public interface Validator<T> {

    boolean validate(T value);

}

