package risk.engine.dto.dto;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/20 17:44
 * @Version: 1.0
 */
@Data
public class IncidentDTO {

    /**
     *
     * 事件code
     */
    private String incidentCode;

    /**
     *
     * 事件名称
     */
    private String incidentName;

    /**
     * 决策结果
     */
    private String decisionResult;

    /**
     * 业务方请求报文
     */
    private String requestPayload;

}
