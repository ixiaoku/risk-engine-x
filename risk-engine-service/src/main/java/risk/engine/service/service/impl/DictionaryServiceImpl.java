package risk.engine.service.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import risk.engine.service.common.dict.OptionsDbFunction;
import risk.engine.service.common.dict.OptionsEnumFunction;
import risk.engine.service.service.IDictionaryService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/20 11:54
 * @Version: 1.0
 */
@Service
public class DictionaryServiceImpl implements IDictionaryService {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Map<String, Object> getList(String[] keys) {
        Map<String, Object> result = new HashMap<>();
        for (String keyStr : keys) {
            String beanName = keyStr + "List";
            OptionsEnumFunction optionsEnumFunction = (OptionsEnumFunction) applicationContext.getBean(beanName);
            result.put(keyStr, optionsEnumFunction.getDictionary());
        }
        return result;
    }

    @Override
    public Map<String, Object> getList(String[] keys, String queryCode) {
        Map<String, Object> result = new HashMap<>();
        for (String keyStr : keys) {
            String beanName = keyStr + "List";
            OptionsDbFunction<String> optionsDbFunction = (OptionsDbFunction<String>) applicationContext.getBean(beanName);
            result.put(keyStr, optionsDbFunction.getDictionary(queryCode));
        }
        return result;
    }

}
