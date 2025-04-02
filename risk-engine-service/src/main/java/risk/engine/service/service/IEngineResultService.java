package risk.engine.service.service;

import risk.engine.db.entity.EngineResult;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.result.EngineExecutorResult;

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

    List<EngineExecutorResult> list(EngineExecutorParam executorParam);


}
