package risk.engine.dto.dto.crawler;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author: X
 * @Date: 2025/4/7 12:12
 * @Version: 1.0
 */
@Getter
@Setter
public class BinanceKLineDTO {

    /**
     * 开始时间
     */
    private long openTime;
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
     * 结束时间
     */
    private long closeTime;
    /**
     * 布林带中轨
     */
    private BigDecimal ma;
    /**
     * 布林带上轨
     */
    private BigDecimal upperBand;
    /**
     * 布林带下轨
     */
    private BigDecimal lowerBand;

    /**
     * 涨跌幅
     */
    private BigDecimal changePercent;

    public BinanceKLineDTO() {
    }

    public BinanceKLineDTO(long openTime, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume, long closeTime) {
        this.openTime = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.closeTime = closeTime;
    }


    public void setBollingerBands(BigDecimal ma, BigDecimal upper, BigDecimal lower) {
        this.ma = ma;
        this.upperBand = upper;
        this.lowerBand = lower;
    }
}
