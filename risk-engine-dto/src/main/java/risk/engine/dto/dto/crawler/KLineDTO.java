package risk.engine.dto.dto.crawler;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author: X
 * @Date: 2025/4/10 11:27
 * @Version: 1.0
 */
@Getter
@Setter
public class KLineDTO {

    private long openTime;      // 开盘时间
    private BigDecimal open;    // 开盘价
    private BigDecimal high;    // 最高价
    private BigDecimal low;     // 最低价
    private BigDecimal close;   // 收盘价
    private BigDecimal volume;  // 成交量
    private long closeTime;     // 收盘时间
    private BigDecimal quoteVolume; // 成交额
    private int tradeCount;     // 成交笔数
    private BigDecimal takerBuyVolume; // 主动买入成交量
    private BigDecimal takerBuyQuoteVolume; // 主动买入成交额
    private String symbol;
    private String interval;

    public KLineDTO(long openTime, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume, long closeTime,
                    BigDecimal quoteVolume, int tradeCount, BigDecimal takerBuyVolume, BigDecimal takerBuyQuoteVolume, String symbol, String interval) {
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
        this.symbol = symbol;
        this.interval = interval;
    }

    public KLineDTO() {
    }
}
