package risk.engine.dto.constant;

/**
 * @Author: X
 * @Date: 2025/3/19 10:18
 * @Version: 1.0
 */

public interface CrawlerConstant {

    String notIceUrl = "https://www.binance.com/bapi/composite/v1/public/market/notice/get?page=1&rows=20";

    String notIceKey = "Binance Futures Will Launch";

    String notIceType = "New Cryptocurrency Listing";

    String notIceCode = "00000";

    String weChatBotUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=4b9616dd-d798-46ba-af63-3bd0cda405a3";

    String notIceBotContent = "监控结果通知\n" +
            ">监控项目:<font color=\"comment\">Binance公告1</font>\n" +
            ">内容:<font color=\"comment\">%s</font>\n" +
            ">时间:<font color=\"comment\">%s</font>";;

}
