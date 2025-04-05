package risk.engine.dto.constant;

/**
 * @Author: X
 * @Date: 2025/3/19 10:18
 * @Version: 1.0
 */

public interface CrawlerConstant {

    String notIceUrl = "https://www.binance.com/bapi/apex/v1/public/apex/cms/article/list/query?type=1&catalogId=48&pageNo=1&pageSize=20";

    String notIceCode = "000000";

    String weChatBotUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";

    String weChatBotDataKey = "FC3DVhg+cBjQ96QATuWRco4PVCK1rg7UFGefsmXPet52p1YGLkHtgg==";

    String notIceBotContent = "监控结果通知\n" +
            ">**标题**:<font color=\"comment\">Binance公告</font>\n" +
            ">**内容**:<font color=\"comment\">%s</font>\n" +
            ">**时间**:<font color=\"comment\">%s</font> \n";

    String notIceLinkBotContent = "监控结果通知\n" +
            ">**标题**:<font color=\"comment\">Binance公告</font>\n" +
            ">**内容**:<font color=\"comment\">%s</font>\n" +
            ">**时间**:<font color=\"comment\">%s</font> \n" +
            ">**人员**：<@DongChunRong> <@carl> <@hdl> <@YuanFeng>";

    String PERSON_BOT_CONTENT = "\uD83D\uDEA8 【监控结果通知】\n" +
            "\uD83D\uDD14 【标题】: Binance公告 \n" +
            "\uD83D\uDD25 【内容】: %s \n" +
            "\uD83D\uDDD3 【时间】: %s \n";

    String ADDRESS_BOT_TITLE = "链名称：%s，发送地址: %s ，收款地址：%s，数量：%s";

    String appId = "wx_ima6EoKmHub0LsKJZwtgi";


}
