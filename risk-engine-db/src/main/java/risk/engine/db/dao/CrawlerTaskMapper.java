package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.CrawlerTaskPO;

import java.util.List;

public interface CrawlerTaskMapper {

    int batchInsert(@Param("list") List<CrawlerTaskPO> recordList);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(CrawlerTaskPO record);

    List<CrawlerTaskPO> selectByExample(CrawlerTaskPO example);

    CrawlerTaskPO selectByPrimaryKey(Long id);

}