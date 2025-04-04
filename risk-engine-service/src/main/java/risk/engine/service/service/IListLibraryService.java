package risk.engine.service.service;

import risk.engine.db.entity.ListLibraryPO;

/**
 * @Author: X
 * @Date: 2025/3/16 12:57
 * @Version: 1.0
 */
public interface IListLibraryService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(ListLibraryPO record);

    ListLibraryPO selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(ListLibraryPO record);

}
