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

    String noticeBotContent = "### 🚨 风控监控结果通知\n" +
            ">**📝 标题：** <font color=\"info\">%s</font>  \n" +
            ">**📄 内容：** <font color=\"comment\">%s</font>  \n" +
            ">**🕒 时间：** <font color=\"warning\">%s</font>  \n";

    String notIceLinkBotContent = "监控结果通知\n" +
            ">**标题**:<font color=\"comment\">Binance公告</font>\n" +
            ">**内容**:<font color=\"comment\">%s</font>\n" +
            ">**时间**:<font color=\"comment\">%s</font> \n" +
            ">**人员**：<@DongChunRong> <@carl> <@hdl> <@YuanFeng>";

    String PERSON_BOT_CONTENT = "\uD83D\uDEA8 【监控结果通知】\n" +
            "\uD83D\uDD14 【标题】: Binance公告 \n" +
            "\uD83D\uDD25 【内容】: %s \n" +
            "\uD83D\uDDD3 【时间】: %s \n";

    String SYSTEM_ALARM_CONTENT =
            "\uD83D\uDEA8 【系统告警】\n" +
            "\uD83D\uDD14 【服务】: %s \n" +
            "\uD83D\uDD25 【环境】: %s \n" +
            "\uD83D\uDCCB 【时间】: %s \n" +
            "\uD83D\uDE80 【标题】: %s \n" +
            "\uD83D\uDEA8 【错误】: %s \n"
            ;

    String ADDRESS_BOT_TITLE = "链名称：%s，发送地址: %s ，收款地址：%s，数量：%s";

    String TRADE_DATA_BOT_TITLE = "15min内，币种交易对：%s, 开盘价: %s, 收盘价：%s, 涨跌幅：%s";

    String CONTRACT_FUNDING_RATE_CONTENT = "5min内币种交易对：%s, 最新资金费率: %s, 标记价格：%s, 指数价格：%s, 下次资金费时间：%s";

    String WINDOW_PRICE_CHANGE_STATISTICS_CONTENT = "5min内，交易对：%s, 价格变化：%s, 涨跌幅百分比：%s, 成交量: %s, 收盘价：%s";

    String OVER_TRANSFER_TITLE = "大额转账";

    String appId = "wx_ima6EoKmHub0LsKJZwtgi";


}
