package risk.engine.db.dao;

import risk.engine.db.entity.ListDataPO;

public interface ListDataMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ListDataPO record);

    ListDataPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ListDataPO record);
}