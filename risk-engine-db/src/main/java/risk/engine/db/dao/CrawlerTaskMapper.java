package risk.engine.db.dao;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.CrawlerTask;

import java.util.List;

public interface CrawlerTaskMapper {

    int batchInsert(@Param("list") List<CrawlerTask> recordList);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(CrawlerTask record);

    List<CrawlerTask> selectByExample(CrawlerTask example);

    CrawlerTask selectByPrimaryKey(Long id);

}