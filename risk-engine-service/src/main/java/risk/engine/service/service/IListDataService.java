package risk.engine.service.service;

import risk.engine.db.entity.ListData;

/**
 * @Author: X
 * @Date: 2025/3/16 12:56
 * @Version: 1.0
 */
public interface IListDataService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(ListData record);

    ListData selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(ListData record);

}
