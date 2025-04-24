package risk.engine.dto.dto.crawler;

import lombok.Data;
import risk.engine.dto.dto.penalty.AnnouncementDTO;

/**
 * @Author: X
 * @Date: 2025/4/24 23:14
 * @Version: 1.0
 */
@Data
public class BewNewsDTO {

    private String url;

    private String title;

    private String sourceName;

    private long timestamp;

    private AnnouncementDTO announcement;

}
