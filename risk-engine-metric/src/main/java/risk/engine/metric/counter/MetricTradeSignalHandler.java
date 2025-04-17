package risk.engine.metric.counter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import risk.engine.common.redis.RedisUtil;
import risk.engine.dto.dto.crawler.KLineDTO;

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
@Slf4j
@Component
public class MetricTradeSignalHandler {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ObjectMapper objectMapper;
    
    @Resource
    private RedisUtil redisUtil;

    private static final int MAX_KLINE_LENGTH = 1000;

    // 存储 K 线数据到 Redis
    public void storeKLine(String key, KLineDTO kLine) throws Exception {
        String kLineJson = objectMapper.writeValueAsString(kLine);
        redisTemplate.opsForList().rightPush(key, kLineJson);
        redisTemplate.opsForList().trim(key, -MAX_KLINE_LENGTH, -1);
    }

    // 从 Redis 获取 K 线数据
    public List<KLineDTO> getKLines(String key, int count) throws Exception {
        List<String> kLineJsons = redisTemplate.opsForList().range(key, -count, -1);
        if (CollectionUtils.isEmpty(kLineJsons)) return List.of();
        List<KLineDTO> kLines = new ArrayList<>();
        for (String json : kLineJsons) {
            kLines.add(objectMapper.readValue(json, KLineDTO.class));
        }
        return kLines;
    }

    // OBV（On-Balance Volume）
    public BigDecimal calculateOBV(List<KLineDTO> kLines, int index) {
        if (index == 0) return BigDecimal.ZERO;
        BigDecimal prevOBV = BigDecimal.ZERO;
        for (int i = 1; i <= index; i++) {
            KLineDTO curr = kLines.get(i);
            KLineDTO prev = kLines.get(i - 1);
            int cmp = curr.getClose().compareTo(prev.getClose());
            if (cmp > 0) {
                prevOBV = prevOBV.add(curr.getVolume());
            } else if (cmp < 0) {
                prevOBV = prevOBV.subtract(curr.getVolume());
            }
            // 相等则不变
        }
        return prevOBV;
    }

    // ASR（Average Speed Ratio）
    public BigDecimal calculateASR(int period, List<KLineDTO> kLines, int index) {
        if (index < period) return BigDecimal.ZERO;
        BigDecimal change = kLines.get(index).getClose().subtract(kLines.get(index - period).getClose()).abs();
        return change.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    /**
     * MA（移动平均线）
     * 描述：计算一段时间内的平均收盘价，用于平滑价格走势。
     * 用法：判断趋势方向（上升/下降）。
     * 多头信号：短期 MA 上穿长期 MA（如 MA20 上穿 MA60）称为“金叉”
     * 空头信号：短期 MA 下穿长期 MA（如 MA20 下穿 MA60）称为“死叉”
     * @param period 参数
     * @param kLines k线
     * @param index index
     * @return 结果
     */
    public BigDecimal calculateMA(int period, List<KLineDTO> kLines, int index) {
        if (index < period - 1) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            sum = sum.add(kLines.get(i).getClose());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    /**
     *  Bollinger Bands（布林带）
     *  描述：由中轨（SMA）、上下轨（±2σ）组成，反映价格波动范围。
     *  用法：衡量波动性及价格是否偏离正常区间。
     *  多头信号：价格向下轨靠近并反弹（低吸机会）
     *  空头信号：价格向上轨靠近并回落（高抛机会）
     *  高波动预警：带宽变宽，可能爆发趋势
     * @param period  period
     * @param stdDevFactor stdDevFactor
     * @param kLines k线数据
     * @param index index
     * @return 结果
     */
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

    /**
     *  RSI（相对强弱指数）
     *  描述：衡量近期上涨和下跌的强度。
     *  范围：0-100
     *  多头信号：RSI < 30，超卖区，可能反弹
     *  空头信号：RSI > 70，超买区，可能回调
     *  趋势确认：持续在 50 上方为多头趋势，持续在 50 下方为空头趋势
     * @param period period
     * @param kLines k线数据
     * @param index index
     * @return 结果
     */
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

    /**
     *  成交量 MA
     *  描述：计算平均成交量，用于判断量能变化趋势。
     *  多头信号：当前成交量 > 均量，量价齐升，趋势强
     *  空头信号：当前成交量 < 均量，价格可能滞涨
     * @param period period
     * @param kLines kLines
     * @param index index
     * @return 结果
     */
    // 计算成交量均线
    public BigDecimal calculateVolumeMA(int period, List<KLineDTO> kLines, int index) {
        if (index < period - 1) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            sum = sum.add(kLines.get(i).getVolume());
        }
        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    /**
     *  ATR（平均真实波幅）
     *  描述：衡量价格波动的强度。
     *  多空判断：
     *  - ATR 升高：波动加大，市场可能进入趋势行情
     *  - ATR 降低：波动收窄，市场可能进入盘整阶段
     *  不直接判断多空，但用于结合趋势判断持仓信心
     * @param period period
     * @param kLines kLines
     * @param index index
     * @return 结果
     */
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

    /**
     * @param period period
     * @param kLines kLines
     * @param index index
     * @return 结果
     */
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

    /**
     *  MACD（指数移动平均线差）
     *  描述：由快线（DIF）、慢线（DEA）、柱状图组成，用于衡量价格动能。
     *  多头信号：DIF 上穿 DEA，柱状图转正（称为“金叉”）
     *  空头信号：DIF 下穿 DEA，柱状图转负（称为“死叉”）
     *  趋势强度：柱状图越大，趋势越强
     * @param kLines kLines
     * @param index index
     * @return 结果
     */
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

    /**
     * KDJ（随机指标）
     * 描述：基于 RSV（未成熟随机值）计算出 K、D、J 值，用于判断短线拐点。
     * 多头信号：K 上穿 D，特别是处于低位（<20）时，称为“金叉”
     * 空头信号：K 下穿 D，特别是处于高位（>80）时，称为“死叉”
     * J > 100 或 < 0 时，视为超买或超卖极端区
     * @param period period
     * @param kLines kLines
     * @param index index
     * @return 结果
     */
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
    public void calculateAndStoreIndicators(String incidentCode, KLineDTO newKLine) {
        try {
            // 存储新 K 线
            String kLineKey = incidentCode + ":" + newKLine.getSymbol() + ":" + newKLine.getInterval();

            storeKLine(kLineKey, newKLine);

            // 获取最近 100 条 K 线数据
            List<KLineDTO> kLines = getKLines(kLineKey, 100);
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
            redisUtil.hSet(incidentCode, hashKey + ":ma20", ma20);
            redisUtil.hSet(incidentCode, hashKey + ":ma60", ma60);
            redisUtil.hSet(incidentCode, hashKey + ":middle", bollingerBands[0]);
            redisUtil.hSet(incidentCode, hashKey + ":upper", bollingerBands[1]);
            redisUtil.hSet(incidentCode, hashKey + ":lower", bollingerBands[2]);
            redisUtil.hSet(incidentCode, hashKey + ":rsi", rsi);
            redisUtil.hSet(incidentCode, hashKey + ":volumeMA", volumeMA);
            redisUtil.hSet(incidentCode, hashKey + ":atr", atr);
            redisUtil.hSet(incidentCode, hashKey + ":macdLine", macd[0]);
            redisUtil.hSet(incidentCode, hashKey + ":macdSignal", macd[1]);
            redisUtil.hSet(incidentCode, hashKey + ":macdHistogram", macd[2]);
            redisUtil.hSet(incidentCode, hashKey + ":kdjK", kdj[0]);
            redisUtil.hSet(incidentCode, hashKey + ":kdjD", kdj[1]);
            redisUtil.hSet(incidentCode, hashKey + ":kdjJ", kdj[2]);
        } catch (Exception e) {
            log.error("错误信息：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}