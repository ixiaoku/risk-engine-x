package risk.engine.service.service;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/20 11:52
 * @Version: 1.0
 */
public interface IDictionaryService {

    Map<String, Object> getList(String[] dictKeyList);

    Map<String, Object> getDictDb(String[] dictKeyList, String queryCode);

}
