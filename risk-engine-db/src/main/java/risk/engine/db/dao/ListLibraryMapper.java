package risk.engine.db.dao;

import risk.engine.db.entity.ListLibraryPO;

public interface ListLibraryMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ListLibraryPO record);

    ListLibraryPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ListLibraryPO record);
}