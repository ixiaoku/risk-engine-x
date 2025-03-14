package risk.engine.dto.dto;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/12 20:17
 * @Version: 1.0
 */
@Data
public class RiskExecuteEngineDTO {

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
