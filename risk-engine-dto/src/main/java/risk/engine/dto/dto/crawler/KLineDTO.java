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

    public KLineDTO(long openTime, String open, String high, String low, String close, String volume,
                 long closeTime, String quoteVolume, int tradeCount, String takerBuyVolume, String takerBuyQuoteVolume) {
        this.openTime = openTime;
        this.open = new BigDecimal(open);
        this.high = new BigDecimal(high);
        this.low = new BigDecimal(low);
        this.close = new BigDecimal(close);
        this.volume = new BigDecimal(volume);
        this.closeTime = closeTime;
        this.quoteVolume = new BigDecimal(quoteVolume);
        this.tradeCount = tradeCount;
        this.takerBuyVolume = new BigDecimal(takerBuyVolume);
        this.takerBuyQuoteVolume = new BigDecimal(takerBuyQuoteVolume);
    }

}
