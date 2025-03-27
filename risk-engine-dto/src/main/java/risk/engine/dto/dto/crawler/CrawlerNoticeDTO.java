package risk.engine.dto.dto.crawler;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/3/27 00:12
 * @Version: 1.0
 */
@Data
public class CrawlerNoticeDTO {
    /**
     * 唯一id
     */
    private String flowNo;
    /**
     * 公告标题
     */
    private String title;
    /**
     * 原贴时间
     */
    private String createdAt;

}
