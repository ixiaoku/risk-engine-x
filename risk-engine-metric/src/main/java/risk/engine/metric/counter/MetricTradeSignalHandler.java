package risk.engine.metric.counter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import risk.engine.dto.dto.crawler.KLineDTO;
import risk.engine.dto.enums.IncidentCodeEnum;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/10 11:30
 * @Version: 1.0
 */
@Component
public class MetricTradeSignalHandler {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    private static final String KLINE_KEY = "kline:btcusdt:15m"; // Redis List 存储 K 线数据
    private static final String INDICATOR_KEY = IncidentCodeEnum.TRADE_QUANT_DATA.getCode() + ":btcusdt:15m"; // Redis Hash 存储指标
    private static final int MAX_KLINE_LENGTH = 1000; // 最多存储 1000 条 K 线数据

    // 存储 K 线数据到 Redis
    public void storeKLine(KLineDTO kLine) throws Exception {
        String kLineJson = objectMapper.writeValueAsString(kLine);
        redisTemplate.opsForList().rightPush(KLINE_KEY, kLineJson);
        redisTemplate.opsForList().trim(KLINE_KEY, -MAX_KLINE_LENGTH, -1);
    }

    // 从 Redis 获取 K 线数据
    public List<KLineDTO> getKLines(int count) throws Exception {
        List<String> kLineJsons = redisTemplate.opsForList().range(KLINE_KEY, -count, -1);
        List<KLineDTO> kLines = new ArrayList<>();
        for (String json : kLineJsons) {
            kLines.add(objectMapper.readValue(json, KLineDTO.class));
        }
        return kLines;
    }

    // 计算 MA（移动平均线）
    public BigDecimal calculateMA(int period, List<KLineDTO> kLines, int index) {
        if (index < period - 1) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            sum = sum.add(kLines.get(i).getClose());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    // 计算布林带
    public BigDecimal[] calculateBollingerBands(int period, BigDecimal stdDevFactor, List<KLineDTO> kLines, int index) {
        if (index < period - 1) return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        // 中轨（SMA）
        BigDecimal middleBand = calculateMA(period, kLines, index);

        // 计算标准差
        BigDecimal sumSquaredDiff = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            BigDecimal diff = kLines.get(i).getClose().subtract(middleBand);
            sumSquaredDiff = sumSquaredDiff.add(diff.multiply(diff));
        }
        BigDecimal variance = sumSquaredDiff.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        BigDecimal stdDev = new BigDecimal(Math.sqrt(variance.doubleValue())).setScale(8, RoundingMode.HALF_UP);

        // 上轨和下轨
        BigDecimal upperBand = middleBand.add(stdDevFactor.multiply(stdDev));
        BigDecimal lowerBand = middleBand.subtract(stdDevFactor.multiply(stdDev));

        return new BigDecimal[]{middleBand, upperBand, lowerBand};
    }

    // 计算 RSI
    public BigDecimal calculateRSI(int period, List<KLineDTO> kLines, int index) {
        if (index < period) return BigDecimal.ZERO;

        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            BigDecimal change = kLines.get(i).getClose().subtract(kLines.get(i - 1).getClose());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                avgGain = avgGain.add(change);
            } else {
                avgLoss = avgLoss.add(change.abs());
            }
        }
        avgGain = avgGain.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        avgLoss = avgLoss.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.valueOf(100);
        BigDecimal rs = avgGain.divide(avgLoss, 8, RoundingMode.HALF_UP);
        BigDecimal hundred = BigDecimal.valueOf(100);
        return hundred.subtract(hundred.divide(BigDecimal.ONE.add(rs), 8, RoundingMode.HALF_UP));
    }

    // 计算成交量均线
    public BigDecimal calculateVolumeMA(int period, List<KLineDTO> kLines, int index) {
        if (index < period - 1) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            sum = sum.add(kLines.get(i).getVolume());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    // 计算 ATR
    public BigDecimal calculateATR(int period, List<KLineDTO> kLines, int index) {
        if (index < period) return BigDecimal.ZERO;
        BigDecimal sumTR = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            KLineDTO current = kLines.get(i);
            KLineDTO previous = kLines.get(i - 1);
            BigDecimal tr1 = current.getHigh().subtract(current.getLow());
            BigDecimal tr2 = current.getHigh().subtract(previous.getClose()).abs();
            BigDecimal tr3 = current.getLow().subtract(previous.getClose()).abs();
            BigDecimal tr = tr1.max(tr2).max(tr3);
            sumTR = sumTR.add(tr);
        }
        return sumTR.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    // 计算 EMA（指数移动平均线，用于 MACD）
    private BigDecimal calculateEMA(int period, List<KLineDTO> kLines, int index) {
        if (index < period - 1) return BigDecimal.ZERO;
        BigDecimal multiplier = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(period + 1), 8, RoundingMode.HALF_UP);
        BigDecimal ema = kLines.get(index - period + 1).getClose(); // 初始值用第一天的收盘价
        for (int i = index - period + 2; i <= index; i++) {
            ema = kLines.get(i).getClose().multiply(multiplier)
                    .add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
        }
        return ema.setScale(8, RoundingMode.HALF_UP);
    }

    // 计算 MACD
    public BigDecimal[] calculateMACD(List<KLineDTO> kLines, int index) {
        if (index < 26 + 9 - 1) return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        // 计算快线和慢线
        BigDecimal ema12 = calculateEMA(12, kLines, index);
        BigDecimal ema26 = calculateEMA(26, kLines, index);
        BigDecimal macdLine = ema12.subtract(ema26);

        // 计算信号线（MACD 线的 9 周期 EMA）
        List<BigDecimal> macdValues = new ArrayList<>();
        for (int i = 25; i <= index; i++) {
            BigDecimal ema12_i = calculateEMA(12, kLines, i);
            BigDecimal ema26_i = calculateEMA(26, kLines, i);
            macdValues.add(ema12_i.subtract(ema26_i));
        }
        BigDecimal signalLine = BigDecimal.ZERO;
        if (macdValues.size() >= 9) {
            signalLine = macdValues.get(macdValues.size() - 9); // 初始值
            BigDecimal multiplier = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(10), 8, RoundingMode.HALF_UP);
            for (int i = macdValues.size() - 8; i < macdValues.size(); i++) {
                signalLine = macdValues.get(i).multiply(multiplier)
                        .add(signalLine.multiply(BigDecimal.ONE.subtract(multiplier)));
            }
        }

        // 计算柱状图
        BigDecimal histogram = macdLine.subtract(signalLine);

        return new BigDecimal[]{macdLine, signalLine, histogram};
    }

    // 计算 KDJ
    public BigDecimal[] calculateKDJ(int period, List<KLineDTO> kLines, int index) {
        if (index < period - 1) return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        // 计算 RSV
        BigDecimal lowestLow = kLines.get(index - period + 1).getLow();
        BigDecimal highestHigh = kLines.get(index - period + 1).getHigh();
        for (int i = index - period + 2; i <= index; i++) {
            lowestLow = lowestLow.min(kLines.get(i).getLow());
            highestHigh = highestHigh.max(kLines.get(i).getHigh());
        }
        BigDecimal close = kLines.get(index).getClose();
        BigDecimal range = highestHigh.subtract(lowestLow);
        BigDecimal rsv = range.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                close.subtract(lowestLow).divide(range, 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        // 计算 K 和 D（这里简化，使用当前 RSV 作为 K 的初始值）
        List<BigDecimal> kValues = new ArrayList<>();
        for (int i = period - 1; i <= index; i++) {
            BigDecimal ll = kLines.get(i - period + 1).getLow();
            BigDecimal hh = kLines.get(i - period + 1).getHigh();
            for (int j = i - period + 2; j <= i; j++) {
                ll = ll.min(kLines.get(j).getLow());
                hh = hh.max(kLines.get(j).getHigh());
            }
            BigDecimal c = kLines.get(i).getClose();
            BigDecimal r = hh.subtract(ll);
            BigDecimal rsv_i = r.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                    c.subtract(ll).divide(r, 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            kValues.add(rsv_i);
        }

        BigDecimal k = BigDecimal.ZERO;
        BigDecimal d = BigDecimal.ZERO;
        if (kValues.size() >= 3) {
            // K 值：3周期 RSV 的 SMA
            k = kValues.subList(kValues.size() - 3, kValues.size()).stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(3), 8, RoundingMode.HALF_UP);

            // D 值：3周期 K 值的 SMA
            List<BigDecimal> dValues = new ArrayList<>();
            for (int i = 2; i < kValues.size(); i++) {
                BigDecimal k_i = kValues.subList(i - 2, i + 1).stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(3), 8, RoundingMode.HALF_UP);
                dValues.add(k_i);
            }
            if (dValues.size() >= 3) {
                d = dValues.subList(dValues.size() - 3, dValues.size()).stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(3), 8, RoundingMode.HALF_UP);
            }
        }

        // J 值
        BigDecimal j = k.multiply(BigDecimal.valueOf(3)).subtract(d.multiply(BigDecimal.valueOf(2)));

        return new BigDecimal[]{k, d, j};
    }

    // 计算所有指标并存储到 Redis
    public void calculateAndStoreIndicators(KLineDTO newKLine) throws Exception {
        // 存储新 K 线
        storeKLine(newKLine);

        // 获取最近 100 条 K 线数据
        List<KLineDTO> kLines = getKLines(100);
        if (kLines.isEmpty()) return;

        int index = kLines.size() - 1;
        long timestamp = newKLine.getOpenTime();

        // 计算指标
        BigDecimal ma20 = calculateMA(20, kLines, index);
        BigDecimal ma60 = calculateMA(60, kLines, index);
        BigDecimal[] bollingerBands = calculateBollingerBands(20, BigDecimal.valueOf(2), kLines, index);
        BigDecimal rsi = calculateRSI(14, kLines, index);
        BigDecimal volumeMA = calculateVolumeMA(20, kLines, index);
        BigDecimal atr = calculateATR(14, kLines, index);
        BigDecimal[] macd = calculateMACD(kLines, index);
        BigDecimal[] kdj = calculateKDJ(9, kLines, index);

        // 存储指标到 Redis Hash
        String hashKey = String.valueOf(timestamp);
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":ma20", ma20.toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":ma60", ma60.toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":bollinger:middle", bollingerBands[0].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":bollinger:upper", bollingerBands[1].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":bollinger:lower", bollingerBands[2].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":rsi", rsi.toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":volumeMA", volumeMA.toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":atr", atr.toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":macd:line", macd[0].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":macd:signal", macd[1].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":macd:histogram", macd[2].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":kdj:k", kdj[0].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":kdj:d", kdj[1].toString());
        redisTemplate.opsForHash().put(INDICATOR_KEY, hashKey + ":kdj:j", kdj[2].toString());
    }
}