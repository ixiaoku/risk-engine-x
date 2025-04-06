package risk.engine.service.service;

import risk.engine.dto.param.DictionaryParam;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/20 11:52
 * @Version: 1.0
 */
public interface IDictionaryService {

    Map<String, Object> getList(String[] keys);

    Map<String, Object> getList(String[] keys, String queryCode);

    Map<String, Object> getDictByDB(DictionaryParam dictionaryParam);
}
