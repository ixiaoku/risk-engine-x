package risk.engine.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: X
 * @Date: 2025/4/1 22:42
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class EngineExecutorParam extends PageHelperQuery{

    /**
     * 业务方唯一id
     */
    private String flowNo;

    /**
     * 风控系统唯一id
     */
    private String riskFlowNo;

    /**
     * 事件
     */
    private String incidentCode;

    /**
     * 规则code
     */
    private String ruleCode;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
