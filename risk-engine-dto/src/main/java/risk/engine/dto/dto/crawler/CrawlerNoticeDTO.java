package risk.engine.dto.dto.crawler;

import lombok.Data;
import risk.engine.dto.dto.penalty.AnnouncementDTO;

/**
 * @Author: X
 * @Date: 2025/3/27 00:12
 * @Version: 1.0
 */
@Data
public class CrawlerNoticeDTO {

    /**
     * 来源
     */
    String noticeSource;
    /**
     * 类型 1发送
     */
    private Integer type;
    /**
     * 公告
     */
    private AnnouncementDTO announcement;

    public static CrawlerNoticeDTO getCrawlerNoticeDTO(String noticeSource, Integer type, String title, String content, String createdAt) {
        CrawlerNoticeDTO crawlerNotice = new CrawlerNoticeDTO();
        AnnouncementDTO announcement = new AnnouncementDTO();
        crawlerNotice.setNoticeSource(noticeSource);
        crawlerNotice.setType(type);
        announcement.setContent(content);
        announcement.setTitle(title);
        announcement.setCreatedAt(createdAt);
        crawlerNotice.setAnnouncement(announcement);
        return crawlerNotice;
    }

}
