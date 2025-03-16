package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 处罚
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class Penalty {

    private Long id;

    private String penaltyCode;

    private String penaltyName;

    private String penaltyDef;

    private Integer status;

    private String operator;

    private String penaltyDescription;

    private String penaltyJson;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}