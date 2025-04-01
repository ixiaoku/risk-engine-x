package risk.engine.dto.param;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/20 14:12
 * @Version: 1.0
 */
@Data
public class DictionaryParam {

    /**
     * 字典key数组
     */
    private String[] dictKeyList;

    /**
     * 字典查询条件
     */
    private String queryCode;

}
