package risk.engine.crawler.monitor.transfer;

import com.alibaba.fastjson2.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.BinanceKLineDTO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BinanceKlineFetcher {
    @Resource
    private ICrawlerTaskService crawlerTaskService;
    private static final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://api.binance.com";
    private static final MathContext MC = new MathContext(8, RoundingMode.HALF_UP);
    private static final BigDecimal TWO = new BigDecimal("2");

    // 获取K线数据
    public List<BinanceKLineDTO> fetchKLines(String symbol, String interval, int limit) {
        String url = BASE_URL + "/api/v3/klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit;
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Unexpected code " + response);
            }
            JsonArray jsonArray = JsonParser.parseString(response.body().string()).getAsJsonArray();
            List<BinanceKLineDTO> klineList = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                JsonArray arr = element.getAsJsonArray();
                klineList.add(new BinanceKLineDTO(
                        arr.get(0).getAsLong(),
                        arr.get(1).getAsBigDecimal(),
                        arr.get(2).getAsBigDecimal(),
                        arr.get(3).getAsBigDecimal(),
                        arr.get(4).getAsBigDecimal(),
                        arr.get(5).getAsBigDecimal(),
                        arr.get(6).getAsLong()
                ));
            }
            return klineList;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    // 计算布林带和涨跌幅，并返回最新一根K线
    public BinanceKLineDTO fetchAndCalculate(String symbol, String interval, int limit, int period) {
        List<BinanceKLineDTO> kLines = fetchKLines(symbol, interval, limit);
        if (kLines == null || kLines.size() < period) {
            log.error("Insufficient data: kLines size = {}, period = {}",
                    kLines == null ? 0 : kLines.size(), period);
            return null;
        }
        // 计算布林带和涨跌幅
        calculateIndicators(kLines, period);
        BinanceKLineDTO binanceKLine =  kLines.get(kLines.size() - 1);
        binanceKLine.setSymbol(symbol);
        return binanceKLine;
    }

    // 计算布林带和涨跌幅
    public void calculateIndicators(List<BinanceKLineDTO> lines, int period) {
        if (lines.size() < period) return;

        for (int i = period - 1; i < lines.size(); i++) {
            List<BigDecimal> closes = lines.subList(i - period + 1, i + 1)
                    .stream()
                    .map(BinanceKLineDTO::getClose)
                    .collect(Collectors.toList());

            // 计算MA（中轨）
            BigDecimal ma = closes.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(period), MC);

            // 计算标准差
            BigDecimal varianceSum = closes.stream()
                    .map(close -> close.subtract(ma, MC).pow(2))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal stdDev = sqrt(varianceSum.divide(BigDecimal.valueOf(period), MC));

            // 设置布林带
            BigDecimal upper = ma.add(stdDev.multiply(TWO, MC), MC);
            BigDecimal lower = ma.subtract(stdDev.multiply(TWO, MC), MC);
            lines.get(i).setBollingerBands(ma, upper, lower);

            // 计算涨跌幅
            BinanceKLineDTO current = lines.get(i);
            BigDecimal changePercent = current.getClose()
                    .subtract(current.getOpen())
                    .divide(current.getOpen(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            current.setChangePercent(changePercent);
        }
    }

    // 平方根计算
    private BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        BigDecimal x = new BigDecimal(Math.sqrt(value.doubleValue()), MC);
        BigDecimal prev;
        do {
            prev = x;
            x = value.divide(x, MC).add(x).divide(BigDecimal.valueOf(2), MC);
        } while (x.subtract(prev).abs().compareTo(new BigDecimal("0.00000001")) > 0);
        return x;
    }

    public void start() {
        BinanceKlineFetcher fetcher = new BinanceKlineFetcher();
        BinanceKLineDTO binanceKLine = fetcher.fetchAndCalculate("BTCUSDT", "5m", 21, 20);
        AnnouncementDTO announcement = new AnnouncementDTO();
        announcement.setTitle("涨跌幅提醒");
        String content = String.format(CrawlerConstant.TRADE_DATA_BOT_TITLE, binanceKLine.getSymbol(), binanceKLine.getOpen(), binanceKLine.getClose(), binanceKLine.getMa(), binanceKLine.getChangePercent());
        announcement.setContent(content);
        announcement.setCreatedAt(DateTimeUtil.getTimeByTimestamp(binanceKLine.getCloseTime()));
        binanceKLine.setAnnouncement(announcement);
        CrawlerTaskPO crawlerTaskPO = new CrawlerTaskPO();
        crawlerTaskPO.setFlowNo(UUID.randomUUID().toString().replace("-", ""));
        crawlerTaskPO.setIncidentCode(IncidentCodeEnum.TRADE_QUANT_DATA.getCode());
        crawlerTaskPO.setRetry(BusinessConstant.RETRY);
        crawlerTaskPO.setRequestPayload(JSON.toJSONString(binanceKLine));
        crawlerTaskPO.setStatus(BusinessConstant.STATUS);
        crawlerTaskPO.setCreateTime(LocalDateTime.now());
        crawlerTaskPO.setUpdateTime(LocalDateTime.now());
        crawlerTaskService.batchInsert(List.of(crawlerTaskPO));
    }

}