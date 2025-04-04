package risk.engine.service.service;

import risk.engine.db.entity.ListDataPO;

/**
 * @Author: X
 * @Date: 2025/3/16 12:56
 * @Version: 1.0
 */
public interface IListDataService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(ListDataPO record);

    ListDataPO selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(ListDataPO record);

}
