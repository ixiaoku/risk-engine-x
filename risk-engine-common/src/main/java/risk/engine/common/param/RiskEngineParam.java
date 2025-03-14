package risk.engine.common.param;

import lombok.Data;
import lombok.NonNull;

/**
 * @Author: X
 * @Date: 2025/3/12 20:42
 * @Version: 1.0
 */
@Data
public class RiskEngineParam {

    /**
     * 唯一id
     */
    private String flowNo;

    /**
     * 业务code
     */
    private String incidentCode;

    /**
     * 请求报文
     */
    private String requestPayload;

}
