package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.CrawlerTaskMapper;
import risk.engine.db.entity.CrawlerTask;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
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
    public Boolean insert(CrawlerTask record) {
        return crawlerTaskMapper.insert(record) > 0;
    }

    @Override
    public Boolean batchInsert(List<CrawlerTask> recordList) {
        return crawlerTaskMapper.batchInsert(recordList) > 0;
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
}
