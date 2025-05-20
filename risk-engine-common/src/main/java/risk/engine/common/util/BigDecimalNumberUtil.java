package risk.engine.common.util;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

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

    public static BigDecimal count(Set<BigDecimal> values) {
        if (CollectionUtils.isEmpty(values)) return BigDecimal.ZERO;
        int count = 0;
        for (BigDecimal val : values) {
            if (val != null) count++;
        }
        return new BigDecimal(count);
    }

    public static BigDecimal sum(Set<BigDecimal> values) {
        if (CollectionUtils.isEmpty(values)) return BigDecimal.ZERO;
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal avg(Set<BigDecimal> values) {
        BigDecimal count = count(values);
        if (count.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return sum(values).divide(count, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal min(Set<BigDecimal> values) {
        if (CollectionUtils.isEmpty(values)) return BigDecimal.ZERO;
        return values.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    public static BigDecimal max(Set<BigDecimal> values) {
        if (CollectionUtils.isEmpty(values)) return BigDecimal.ZERO;
        return values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

}
