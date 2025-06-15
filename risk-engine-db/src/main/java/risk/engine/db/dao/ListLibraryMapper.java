package risk.engine.db.dao;

import risk.engine.db.entity.ListLibraryPO;
import risk.engine.db.entity.example.ListLibraryExample;

import java.util.List;

public interface ListLibraryMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ListLibraryPO record);

    ListLibraryPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ListLibraryPO record);

    List<ListLibraryPO> selectByExample(ListLibraryExample example);

}