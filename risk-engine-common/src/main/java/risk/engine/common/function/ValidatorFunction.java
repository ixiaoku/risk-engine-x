package risk.engine.common.function;

import risk.engine.dto.exception.RiskException;

/**
 * @Author: X
 * @Date: 2025/3/12 21:09
 * @Version: 1.0
 */
@FunctionalInterface
public interface ValidatorFunction<T> {

    void validateException(T value) throws RiskException;

}
