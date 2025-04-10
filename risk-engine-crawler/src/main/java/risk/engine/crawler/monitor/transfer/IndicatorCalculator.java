package risk.engine.crawler.monitor.transfer;

/**
 * @Author: X
 * @Date: 2025/4/10 11:30
 * @Version: 1.0
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import risk.engine.dto.dto.crawler.KLineDTO;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class IndicatorCalculator {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    private static final String KLineDTO_KEY = "KLineDTO:btcusdt:5m"; // Redis List 存储 K 线数据
    private static final String INDICATOR_KEY = "indicator:btcusdt:5m"; // Redis Hash 存储指标
    private static final int MAX_KLineDTO_LENGTH = 1000; // 最多存储 1000 条 K 线数据

    // 存储 K 线数据到 Redis
    public void storeKLineDTO(KLineDTO kLine) throws Exception {
        String KLineDTOJson = objectMapper.writeValueAsString(kLine);
        redisTemplate.opsForList().rightPush(KLineDTO_KEY, KLineDTOJson);
        // 限制 List 长度，避免内存过大
        redisTemplate.opsForList().trim(KLineDTO_KEY, -MAX_KLineDTO_LENGTH, -1);
    }

    // 从 Redis 获取 K 线数据
    public List<KLineDTO> getKLineDTOs(int count) throws Exception {
        List<String> kLineDTOJsons = redisTemplate.opsForList().range(KLineDTO_KEY, -count, -1);
        List<KLineDTO> KLineDTOs = new ArrayList<>();
        for (String json : kLineDTOJsons) {
            KLineDTOs.add(objectMapper.readValue(json, KLineDTO.class));
        }
        return KLineDTOs;
    }

    // 计算 MA（移动平均线）
    public BigDecimal calculateMA(int period, List<KLineDTO> KLineDTOs, int index) {
        if (index < period - 1) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            sum = sum.add(KLineDTOs.get(i).getClose());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    // 计算布林带
    public BigDecimal[] calculateBollingerBands(int period, BigDecimal stdDevFactor, List<KLineDTO> KLineDTOs, int index) {
        if (index < period - 1) return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        // 中轨（SMA）
        BigDecimal middleBand = calculateMA(period, KLineDTOs, index);

        // 计算标准差
        BigDecimal sumSquaredDiff = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            BigDecimal diff = KLineDTOs.get(i).getClose().subtract(middleBand);
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
    public BigDecimal calculateRSI(int period, List<KLineDTO> KLineDTOs, int index) {
        if (index < period) return BigDecimal.ZERO;

        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            BigDecimal change = KLineDTOs.get(i).getClose().subtract(KLineDTOs.get(i - 1).getClose());
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
    public BigDecimal calculateVolumeMA(int period, List<KLineDTO> KLineDTOs, int index) {
        if (index < period - 1) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            sum = sum.add(KLineDTOs.get(i).getVolume());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    // 计算 ATR
    public BigDecimal calculateATR(int period, List<KLineDTO> KLineDTOs, int index) {
        if (index < period) return BigDecimal.ZERO;
        BigDecimal sumTR = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            KLineDTO current = KLineDTOs.get(i);
            KLineDTO previous = KLineDTOs.get(i - 1);
            BigDecimal tr1 = current.getHigh().subtract(current.getLow());
            BigDecimal tr2 = current.getHigh().subtract(previous.getClose()).abs();
            BigDecimal tr3 = current.getLow().subtract(previous.getClose()).abs();
            BigDecimal tr = tr1.max(tr2).max(tr3);
            sumTR = sumTR.add(tr);
        }
        return sumTR.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    // 计算所有指标并存储到 Redis
    public void calculateAndStoreIndicators(KLineDTO newKLineDTO) throws Exception {
        // 存储新 K 线
        storeKLineDTO(newKLineDTO);

        // 获取最近 100 条 K 线数据
        List<KLineDTO> KLineDTOs = getKLineDTOs(100);
        if (KLineDTOs.isEmpty()) return;

        int index = KLineDTOs.size() - 1;
        long timestamp = newKLineDTO.getOpenTime();

        // 计算指标
        BigDecimal ma20 = calculateMA(20, KLineDTOs, index);
        BigDecimal ma60 = calculateMA(60, KLineDTOs, index);
        BigDecimal[] bollingerBands = calculateBollingerBands(20, BigDecimal.valueOf(2), KLineDTOs, index);
        BigDecimal rsi = calculateRSI(14, KLineDTOs, index);
        BigDecimal volumeMA = calculateVolumeMA(20, KLineDTOs, index);
        BigDecimal atr = calculateATR(14, KLineDTOs, index);

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
    }
}
