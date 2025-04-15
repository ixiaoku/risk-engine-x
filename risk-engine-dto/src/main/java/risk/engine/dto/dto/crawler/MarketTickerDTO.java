package risk.engine.dto.dto.crawler;

import lombok.Data;
import risk.engine.dto.dto.penalty.AnnouncementDTO;

import java.math.BigDecimal;

/**
 * @Author: X
 * @Date: 2025/4/15 12:49
 * @Version: 1.0
 */
@Data
public class MarketTickerDTO {
    // 交易对
    private String symbol;

    // 绝对价格变动
    private BigDecimal priceChange;

    // 相对价格变动百分比
    private BigDecimal priceChangePercent;

    /**
     * 上涨百分比
     */
    private BigDecimal upPriceChangePercent;

    /**
     * 下跌百分比
     */
    private BigDecimal downPriceChangePercent;

    // 报价成交量 / 成交量（加权平均价）
    private BigDecimal weightedAvgPrice;

    // 开盘价
    private BigDecimal openPrice;

    // 最高价
    private BigDecimal highPrice;

    // 最低价
    private BigDecimal lowPrice;

    // 最后成交价
    private BigDecimal lastPrice;

    // 基础资产的成交量
    private BigDecimal volume;

    // 报价资产的成交量
    private BigDecimal quoteVolume;

    // 开盘时间（毫秒时间戳）
    private long openTime;

    // 收盘时间（毫秒时间戳）
    private long closeTime;

    // 区间内的第一个交易的交易ID
    private long firstId;

    // 区间内的最后一个交易的交易ID
    private long lastId;

    // 区间内的交易数量
    private long count;

    private AnnouncementDTO announcement;
}

