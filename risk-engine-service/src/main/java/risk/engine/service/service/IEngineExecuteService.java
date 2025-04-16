package risk.engine.service.service;

import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.vo.RiskEngineExecuteVO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/14 12:16
 * @Version: 1.0
 */
public interface IEngineExecuteService {

    RiskEngineExecuteVO execute(RiskEngineParam riskEngineParam);

    RiskEngineExecuteVO executeBatch(List<RiskEngineParam> riskEngineParam);

}
