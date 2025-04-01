package risk.engine.dto.result;

import lombok.Data;
import risk.engine.dto.dto.rule.IndicatorDTO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/20 23:28
 * @Version: 1.0
 */
@Data
public class IncidentResult {

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
     * 事件状态 状态（0：删除，1：上线，2：下线）
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
     * 业务方请求报文
     */
    private String requestPayload;

    /**
     * 事件接入配置的字段 和关联requestPayload
     */
    private List<IndicatorDTO> indicators;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

}
