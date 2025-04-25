package risk.engine.dto.dto.crawler;

import lombok.Data;
import risk.engine.dto.dto.penalty.AnnouncementDTO;

/**
 * @Author: X
 * @Date: 2025/4/25 16:42
 * @Version: 1.0
 */
@Data
public class FundingRateDTO {

    /**
     * 交易对，例如BTCUSDT表示比特币对泰达币的交易对。
     */
    private String symbol;

    /**
     * 标记价格，用于计算资金费率等。
     */
    private String markPrice;

    /**
     * 指数价格，通常用于参考的市场价格。
     */
    private String indexPrice;

    /**
     * 预估结算价，仅在交割开始前最后一小时有意义。
     */
    private String estimatedSettlePrice;

    /**
     * 最近更新的资金费率，用于计算资金费用。
     */
    private String lastFundingRate;

    /**
     * 标的资产基础利率，通常用于计算资金费用的一部分。
     */
    private String interestRate;

    /**
     * 下次资金费时间的时间戳，单位为毫秒。
     */
    private long nextFundingTime;

    /**
     * 数据更新时间的时间戳，单位为毫秒。
     */
    private long time;

    private AnnouncementDTO announcement;

}
