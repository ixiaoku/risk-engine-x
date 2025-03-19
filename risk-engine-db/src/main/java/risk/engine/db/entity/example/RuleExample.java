package risk.engine.db.entity.example;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/19 17:31
 * @Version: 1.0
 */
@Data
public class RuleExample {

    /**
     * 一页多少条
     */
    private int pageSize;

    /**
     * 页数
     */
    private int pageNum;

    /**
     * 事件code
     */
    private String incidentCode;

    /**
     * 规则code
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则状态 状态（0：删除，1：上线，2：下线，3：模拟）
     */
    private Integer status;

}
