package risk.engine.db.dao;

import risk.engine.db.entity.ListLibrary;

public interface ListLibraryMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ListLibrary record);

    ListLibrary selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ListLibrary record);
}