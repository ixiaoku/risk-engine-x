package risk.engine.service.service;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.CrawlerTask;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/23 18:09
 * @Version: 1.0
 */
public interface ICrawlerTaskService {

    Boolean insert(CrawlerTask record);

    Boolean batchInsert(@Param("list") List<CrawlerTask> recordList);

    Boolean deleteByPrimaryKey(Long id);

    Boolean updateByPrimaryKey(CrawlerTask record);

    List<CrawlerTask> selectByExample(CrawlerTask example);

    CrawlerTask selectByPrimaryKey(Long id);

    CrawlerTask getCrawlerTask(String flowNo, String incidentCode, String requestPayload);

}
