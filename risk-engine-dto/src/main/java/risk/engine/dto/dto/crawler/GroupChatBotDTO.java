package risk.engine.dto.dto.crawler;

import lombok.Data;

/**
 *
 {
 "msgtype": "markdown",
 "markdown": {
 "content": "监控结果通知
 >监控项目:<font color=\"comment\">Binance公告</font>
 >内容:<font color=\"comment\">%s</font>
 >时间:<font color=\"comment\">%s</font>"
 }
 }
 * @Author: X
 * @Date: 2025/3/18 14:18
 * @Version: 1.0
 */
@Data
public class GroupChatBotDTO {

    private String msgtype;

    public Markdown markdown;

    @Data
    public static class Markdown {
        String content;
    }

}
