package risk.engine.service.common.dict;

import java.util.List;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/12 21:03
 * @Version: 1.0
 */
@FunctionalInterface
public interface OptionsEnumFunction {

    List<Map<String, Object>> getDictionary();

}

