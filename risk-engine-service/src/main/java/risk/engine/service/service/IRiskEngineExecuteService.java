package risk.engine.service.service;

import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.result.RiskEngineExecuteResult;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/14 12:16
 * @Version: 1.0
 */
public interface IRiskEngineExecuteService {

    RiskEngineExecuteResult execute(RiskEngineParam riskEngineParam);

    RiskEngineExecuteResult executeBatch(List<RiskEngineParam> riskEngineParam);

}
