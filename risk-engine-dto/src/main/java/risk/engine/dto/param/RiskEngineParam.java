package risk.engine.dto.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;


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
    @NotBlank(message = "flowNo不能为空")
    private String flowNo;

    /**
     * 业务code
     */
    @NotBlank(message = "incidentCode不能为空")
    private String incidentCode;

    /**
     * 请求报文
     */
    @NotBlank(message = "requestPayload不能为空")
    private String requestPayload;

}
