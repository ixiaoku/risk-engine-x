package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.ListDataMapper;
import risk.engine.db.entity.ListDataPO;
import risk.engine.service.service.IListDataService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 12:56
 * @Version: 1.0
 */
@Service
public class ListDataServiceImpl implements IListDataService {

    @Resource
    private ListDataMapper listDataMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return listDataMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(ListDataPO record) {
        return listDataMapper.insert(record) > 0;
    }

    @Override
    public ListDataPO selectByPrimaryKey(Long id) {
        return listDataMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(ListDataPO record) {
        return listDataMapper.updateByPrimaryKey(record) > 0;
    }
}
