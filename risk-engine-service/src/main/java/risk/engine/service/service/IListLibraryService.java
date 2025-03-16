package risk.engine.service.service;

import risk.engine.db.entity.ListLibrary;

/**
 * @Author: X
 * @Date: 2025/3/16 12:57
 * @Version: 1.0
 */
public interface IListLibraryService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(ListLibrary record);

    ListLibrary selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(ListLibrary record);

}
