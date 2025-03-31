package risk.engine.service.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import risk.engine.db.dao.CrawlerTaskMapper;
import risk.engine.db.entity.CrawlerTask;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.enums.TaskStatusEnum;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/23 18:10
 * @Version: 1.0
 */
@Service
public class CrawlerTaskServiceImpl implements ICrawlerTaskService {

    @Resource
    private CrawlerTaskMapper crawlerTaskMapper;

    @Override
    public void batchInsert(List<CrawlerTask> recordList) {
        crawlerTaskMapper.batchInsert(recordList);
    }

    @Override
    public Boolean deleteByPrimaryKey(Long id) {
        return crawlerTaskMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public Boolean updateByPrimaryKey(CrawlerTask record) {
        return crawlerTaskMapper.updateByPrimaryKey(record) > 0;
    }

    @Override
    public List<CrawlerTask> selectByExample(CrawlerTask example) {
        return crawlerTaskMapper.selectByExample(example);
    }

    @Override
    public CrawlerTask selectByPrimaryKey(Long id) {
        return crawlerTaskMapper.selectByPrimaryKey(id);
    }

    @Override
    public CrawlerTask getCrawlerTask(String flowNo, String incidentCode, String requestPayload) {
        if (StringUtils.isEmpty(flowNo) || StringUtils.isEmpty(incidentCode) || StringUtils.isEmpty(requestPayload)) {
            return null;
        }
        //去重 重复的不保存
        CrawlerTask taskQuery = new CrawlerTask();
        taskQuery.setFlowNo(flowNo);
        taskQuery.setIncidentCode(incidentCode);
        List<CrawlerTask> crawlerTaskList = crawlerTaskMapper.selectByExample(taskQuery);
        if (CollectionUtils.isNotEmpty(crawlerTaskList)) {
            return null;
        }
        //组装爬虫数据
        CrawlerTask crawlerTask = new CrawlerTask();
        crawlerTask.setFlowNo(flowNo);
        crawlerTask.setIncidentCode(incidentCode);
        crawlerTask.setStatus(TaskStatusEnum.WAIT.getCode());
        crawlerTask.setRetry(BusinessConstant.RETRY);
        crawlerTask.setRequestPayload(requestPayload);
        crawlerTask.setCreateTime(LocalDateTime.now());
        crawlerTask.setUpdateTime(LocalDateTime.now());
        return crawlerTask;
    }
}
