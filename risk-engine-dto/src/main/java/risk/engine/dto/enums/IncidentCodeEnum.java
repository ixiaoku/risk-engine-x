package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 处罚类型
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum IncidentCodeEnum {

    BINANCE_NOTICE_LIST("BinanceNoticeList", "币安公告监控"),
    TWITTER_USER_RELEASE_LIST("TwitterUserReleaseList", "推特名人监控"),
    TRANSFER_CHAIN("ChainTransfer", "链上转账"),
    TRADE_QUANT_DATA("TradeQuantData", "交易量化"),
    TELEGRAM_BEW_NEWS("TelegramBEWnews", "TG机器人新闻推送"),
    WINDOW_PRICE_CHANGE_STATISTICS("WindowPriceChangeStatistics", "滚动窗口价格变动统计"),
    ;

    /**
     * 枚举的整数值
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    // 枚举的构造函数，用于设置整数值
    IncidentCodeEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static IncidentCodeEnum getListTypeEnumByCode(String code) {
        for (IncidentCodeEnum status : IncidentCodeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
