package risk.engine.db.dao;

import risk.engine.db.entity.ListData;

public interface ListDataMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ListData record);

    ListData selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ListData record);
}