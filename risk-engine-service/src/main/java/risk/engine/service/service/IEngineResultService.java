package risk.engine.service.service;

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

    boolean deleteByPrimaryKey(Long id);

    boolean insert(EngineResultPO record);

    List<EngineResultPO> selectByExample(EngineResultPO engineResult);

    EngineResultPO selectByPrimaryKey(Long id);

    List<EngineExecutorVO> list(EngineExecutorParam executorParam);


}
