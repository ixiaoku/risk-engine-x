package risk.engine.service.service;

import risk.engine.db.entity.EngineResult;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
public interface IEngineResultService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(EngineResult record);

    List<EngineResult> selectByExample(EngineResult engineResult);

    EngineResult selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(EngineResult record);

}
