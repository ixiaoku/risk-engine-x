package risk.engine.service.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import risk.engine.service.common.OptionsEnumFunction;
import risk.engine.service.service.IDictionaryService;

import javax.annotation.Resource;
import java.util.List;
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
    public List<Map<String, Object>> getList(String key) {
        OptionsEnumFunction optionsEnumFunction = (OptionsEnumFunction) applicationContext.getBean(key);
        return optionsEnumFunction.getDictionary();
    }

}
