package risk.engine.service.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import risk.engine.db.dao.CrawlerTaskMapper;
import risk.engine.db.dao.KLineMapper;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.db.entity.KLinePO;
import risk.engine.service.service.IKLineService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/10 14:58
 * @Version: 1.0
 */
@Service
public class KLineServiceImpl implements IKLineService {

    @Resource
    private KLineMapper kLineMapper;

    @Resource
    private CrawlerTaskMapper crawlerTaskMapper;


    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return kLineMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInsert(List<CrawlerTaskPO> crawlerTaskPOList, List<KLinePO> list) {
        List<KLinePO> kLinePOList = new ArrayList<>();
        for (KLinePO kLinePO : list) {
            KLinePO kLineQuery = new KLinePO();
            kLineQuery.setOpenTime(kLinePO.getOpenTime());
            kLineQuery.setSymbol(kLinePO.getSymbol());
            List<KLinePO> kLineQueryList = kLineMapper.selectByExample(kLineQuery);
            if (CollectionUtils.isEmpty(kLineQueryList)) {
                kLinePOList.add(kLinePO);
            }
        }
        if(CollectionUtils.isNotEmpty(crawlerTaskPOList)) {
            kLineMapper.batchInsert(kLinePOList);
        }
        //crawlerTaskMapper.batchInsert(crawlerTaskPOList);
        return Boolean.TRUE;
    }

    @Override
    public List<KLinePO> selectByExample(KLinePO kLine) {
        return kLineMapper.selectByExample(kLine);
    }

    @Override
    public boolean updateByPrimaryKey(KLinePO kLine) {
        return kLineMapper.updateByPrimaryKey(kLine) > 0;
    }
}
