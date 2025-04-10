package risk.engine.db.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * k线
 * @Author: X
 * @Date: 2025/4/10 19:35
 * @Version: 1.0
 */
@Data
public class KLinePO {

    /**
     * 主键
     */
    private Long id;
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
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public KLinePO(String symbol, String interval, Long openTime, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume, Long closeTime, BigDecimal quoteVolume, Integer tradeCount, BigDecimal takerBuyVolume, BigDecimal takerBuyQuoteVolume, LocalDateTime createTime, LocalDateTime updateTime) {
        this.symbol = symbol;
        this.interval = interval;
        this.openTime = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.closeTime = closeTime;
        this.quoteVolume = quoteVolume;
        this.tradeCount = tradeCount;
        this.takerBuyVolume = takerBuyVolume;
        this.takerBuyQuoteVolume = takerBuyQuoteVolume;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public KLinePO() {
    }

}