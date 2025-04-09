package risk.engine.service.service;

import org.apache.commons.lang3.tuple.Pair;
import risk.engine.db.entity.EngineResultPO;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
import risk.engine.dto.vo.ReplyRuleVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
public interface IEngineResultService {

    boolean insert(EngineResultPO record);

    Map<String, BigDecimal> getDashboard(EngineExecutorParam executorParam);

    EngineExecutorVO getOne(EngineExecutorParam executorParam);

    Pair<List<EngineExecutorVO>, Long> list(EngineExecutorParam executorParam);

    ReplyRuleVO replay(EngineExecutorParam executorParam);

}
