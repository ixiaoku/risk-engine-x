package risk.engine.dto.dto.publish;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: X
 * @Date: 2025/3/16 08:52
 * @Version: 1.0
 */
@Data
public class RiskPenalty {

    /**
     * 处置code
     */
    private String penaltyCode;

    /**
     * 处置名称
     */
    private String penaltyName;

    /**
     * 处置定义 实例化的时候根据路径
     */
    private String penaltyDef;

    /**
     * 处置描述
     */
    private String penaltyDescription;

    /**
     * 处置报文
     */
    private String penaltyJson;

    /**
     * 处置时间
     */
    private LocalDateTime penalty_time;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
