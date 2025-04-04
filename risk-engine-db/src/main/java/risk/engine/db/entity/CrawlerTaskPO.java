package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 爬虫数据过引擎
 * @Author: X
 * @Date: 2025/3/23 16:35
 * @Version: 1.0
 */
@Data
public class CrawlerTaskPO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 流水号
     */
    private String flowNo;

    /**
     * 事件code
     */
    private String incidentCode;

    /**
     * 状态 0未同步 1同步
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retry;

    /**
     * 请求参数
     */
    private String requestPayload;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}