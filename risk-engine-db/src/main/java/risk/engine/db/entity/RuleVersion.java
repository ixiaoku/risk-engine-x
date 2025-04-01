package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RuleVersion {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 规则code
     */
    private String ruleCode;
    /**
     * 规则状态 状态（0：删除，1：上线，2：下线，3：模拟）
     */
    private Integer status;
    /**
     *  配置的逻辑表达式 1 && 2 || 3
     */
    private String logicScript;
    /**
     * groovy可执行的表达式
     */
    private String groovyScript;
    /**
     * json表达式
     */
    private String jsonScript;
    /**
     * 版本号
     */
    private String version;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}