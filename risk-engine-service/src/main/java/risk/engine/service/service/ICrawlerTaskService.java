package risk.engine.service.service;

import org.apache.ibatis.annotations.Param;
import risk.engine.db.entity.CrawlerTaskPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/23 18:09
 * @Version: 1.0
 */
public interface ICrawlerTaskService {

    void batchInsert(@Param("list") List<CrawlerTaskPO> recordList);

    Boolean deleteByPrimaryKey(Long id);

    Boolean updateByPrimaryKey(CrawlerTaskPO record);

    List<CrawlerTaskPO> selectByExample(CrawlerTaskPO example);

    CrawlerTaskPO selectByPrimaryKey(Long id);

    CrawlerTaskPO getCrawlerTask(String flowNo, String incidentCode, String requestPayload);

}
