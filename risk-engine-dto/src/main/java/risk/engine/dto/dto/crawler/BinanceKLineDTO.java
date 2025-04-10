package risk.engine.dto.dto.crawler;

import lombok.Data;
import risk.engine.dto.dto.penalty.AnnouncementDTO;

import java.math.BigDecimal;

/**
 * @Author: X
 * @Date: 2025/4/7 12:12
 * @Version: 1.0
 */
@Data
public class BinanceKLineDTO {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 时间间隔 5min
     */
    private String interval;
    /**
     * 开盘时间
     */
    private Long openTime;
    /**
     * 开盘价
     */
    private BigDecimal open;
    /**
     * 最高价
     */
    private BigDecimal high;
    /**
     * 最低价
     */
    private BigDecimal low;
    /**
     * 收盘价
     */
    private BigDecimal close;
    /**
     * 成交量
     */
    private BigDecimal volume;
    /**
     * 收盘时间
     */
    private Long closeTime;
    /**
     * 成交额
     */
    private BigDecimal quoteVolume;
    /**
     * 成交笔数
     */
    private Integer tradeCount;
    /**
     * 主动买入成交量
     */
    private BigDecimal takerBuyVolume;
    /**
     * 主动买入成交额
     */
    private BigDecimal takerBuyQuoteVolume;
    /**
     * 公告
     */
    private AnnouncementDTO announcement;
    /**
     * 涨幅
     */
    private BigDecimal upChangePercent;
    /**
     * 跌幅
     */
    private BigDecimal downChangePercent;

}
