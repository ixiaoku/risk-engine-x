package risk.engine.dto.result;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/12 20:43
 * @Version: 1.0
 */
@Data
public class RiskEngineExecuteResult {
    /**
     * 1通过 0拒绝
     */
    private String decisionResult;
}
