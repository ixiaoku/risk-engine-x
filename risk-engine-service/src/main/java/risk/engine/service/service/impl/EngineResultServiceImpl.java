package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.EngineResultMapper;
import risk.engine.db.entity.EngineResult;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
@Service
public class EngineResultServiceImpl implements IEngineResultService {

    @Resource
    private EngineResultMapper engineResultMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return engineResultMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(EngineResult record) {
        return engineResultMapper.insert(record) > 0;
    }

    @Override
    public List<EngineResult> selectByExample(EngineResult engineResult) {
        return engineResultMapper.selectByExample(engineResult);
    }

    @Override
    public EngineResult selectByPrimaryKey(Long id) {
        return engineResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(EngineResult record) {
        return engineResultMapper.updateByPrimaryKey(record) > 0;
    }
}
