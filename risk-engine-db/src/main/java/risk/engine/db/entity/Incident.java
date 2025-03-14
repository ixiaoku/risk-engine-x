package risk.engine.db.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 事件
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class Incident {
    /**
     * 主键id
     */
    private Long id;

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
     * 事件状态
     */
    private Integer status;

    /**
     * 责任人
     */
    private String responsiblePerson;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 业务方请求报文
     */
    private String requestPayload;
}
