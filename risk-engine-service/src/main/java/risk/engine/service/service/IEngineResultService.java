package risk.engine.service.service;

import org.apache.commons.lang3.tuple.Pair;
import risk.engine.db.entity.EngineResultPO;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
public interface IEngineResultService {

    boolean insert(EngineResultPO record);

    EngineExecutorVO getOne(EngineExecutorParam executorParam);

    Pair<List<EngineExecutorVO>, Long> list(EngineExecutorParam executorParam);

}
