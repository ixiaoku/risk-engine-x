package risk.engine.common.function;

/**
 * @Author: X
 * @Date: 2025/3/12 21:09
 * @Version: 1.0
 */
@FunctionalInterface
public interface ValidatorException<T> {

    void validateException(T value) throws Exception;

}
