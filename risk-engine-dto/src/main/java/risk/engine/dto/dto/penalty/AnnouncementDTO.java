package risk.engine.dto.dto.penalty;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/4/6 14:51
 * @Version: 1.0
 */
@Data
public class AnnouncementDTO {

    /**
     * 发布标题
     */
    private String title;

    /**
     * 发布内容
     */
    private String content;

    /**
     * 发布时间
     */
    private String createdAt;

    public AnnouncementDTO() {
    }

    public AnnouncementDTO(String title, String content, String createdAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
