package risk.engine.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Author: X
 * @Date: 2025/4/10 15:14
 * @Version: 1.0
 */
public class BigDecimalNumberUtil {

    /**
     * 计算涨跌幅百分比，结果保留 2 位小数（四舍五入）
     * (close - open) / open * 100
     */
    public static BigDecimal calcChangePercent(BigDecimal open, BigDecimal close) {
        if (open == null || close == null || open.compareTo(BigDecimal.ZERO) == 0 || close.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal change = close.subtract(open);
        return change
                .divide(open, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
