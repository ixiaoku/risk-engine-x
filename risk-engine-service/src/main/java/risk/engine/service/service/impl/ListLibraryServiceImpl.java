package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.ListLibraryMapper;
import risk.engine.db.entity.ListLibrary;
import risk.engine.service.service.IListLibraryService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 12:57
 * @Version: 1.0
 */
@Service
public class ListLibraryServiceImpl implements IListLibraryService {

    @Resource
    private ListLibraryMapper listLibraryMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return listLibraryMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(ListLibrary record) {
        return listLibraryMapper.insert(record) > 0;
    }

    @Override
    public ListLibrary selectByPrimaryKey(Long id) {
        return listLibraryMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(ListLibrary record) {
        return listLibraryMapper.updateByPrimaryKey(record) > 0;
    }
}
