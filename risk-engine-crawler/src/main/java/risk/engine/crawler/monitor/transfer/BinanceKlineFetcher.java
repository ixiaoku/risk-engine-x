package risk.engine.crawler.monitor.transfer;

import com.alibaba.fastjson2.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.redis.RedisUtil;
import risk.engine.common.util.BigDecimalNumberUtil;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.db.entity.KLinePO;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.BinanceKLineDTO;
import risk.engine.dto.dto.crawler.KLineDTO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
import risk.engine.service.service.ICrawlerTaskService;
import risk.engine.service.service.IKLineService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BinanceKlineFetcher {

    @Resource
    private ICrawlerTaskService crawlerTaskService;

    @Resource
    private IKLineService klineService;

    @Resource
    private RedisUtil redisUtil;

    private static final String BASE_URL = "https://api.binance.com";

    // 获取K线数据
    public List<KLineDTO> fetchKLines(String symbol, String interval, int limit) {
        String url = BASE_URL + "/api/v3/klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit;
        String result = OkHttpUtil.get(url);
        if (StringUtils.isEmpty(result)) return List.of();
        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
        List<KLineDTO> klineList = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonArray arr = element.getAsJsonArray();
            KLineDTO kLine = new KLineDTO(
                    arr.get(0).getAsLong(),
                    arr.get(1).getAsBigDecimal(),
                    arr.get(2).getAsBigDecimal(),
                    arr.get(3).getAsBigDecimal(),
                    arr.get(4).getAsBigDecimal(),
                    arr.get(5).getAsBigDecimal(),
                    arr.get(6).getAsLong(),
                    arr.get(7).getAsBigDecimal(),
                    arr.get(8).getAsInt(),
                    arr.get(9).getAsBigDecimal(),
                    arr.get(10).getAsBigDecimal(),
                    symbol,
                    interval);
            klineList.add(kLine);
        }
        return klineList;
    }

    public void start() {
        List<String> symbols = List.of("BTCUSDT", "ETHUSDT");
        String interval = "15m";
        for (String symbol : symbols) {
            List<KLineDTO> binanceKLineList = fetchKLines(symbol, interval, 2);
            if(CollectionUtils.isEmpty(binanceKLineList)) return;
            KLineDTO kLinePO = binanceKLineList.get(binanceKLineList.size() - 1);
            BinanceKLineDTO binanceKLineDTO = new BinanceKLineDTO();
            BeanUtils.copyProperties(kLinePO, binanceKLineDTO);
            AnnouncementDTO announcement = new AnnouncementDTO();
            announcement.setTitle("涨跌幅提醒");
            BigDecimal changePercent = BigDecimalNumberUtil.calcChangePercent(kLinePO.getOpen(), kLinePO.getClose());
            binanceKLineDTO.setUpChangePercent(changePercent.compareTo(BigDecimal.ZERO) > 0 ? changePercent : BigDecimal.ZERO);
            binanceKLineDTO.setDownChangePercent(changePercent.compareTo(BigDecimal.ZERO) < 0 ? changePercent : BigDecimal.ZERO);
            String content = String.format(CrawlerConstant.TRADE_DATA_BOT_TITLE, kLinePO.getSymbol(), kLinePO.getOpen(), kLinePO.getClose(), changePercent);
            announcement.setContent(content);
            announcement.setCreatedAt(DateTimeUtil.getTimeByTimestamp(kLinePO.getCloseTime()));
            binanceKLineDTO.setAnnouncement(announcement);
            CrawlerTaskPO crawlerTaskPO = crawlerTaskService.getCrawlerTask(kLinePO.getSymbol() + kLinePO.getOpenTime(),
                    IncidentCodeEnum.TRADE_QUANT_DATA.getCode(), JSON.toJSONString(binanceKLineDTO)
            );
            if(Objects.isNull(crawlerTaskPO)) return;

            List<KLinePO> kLinePOList = binanceKLineList.stream()
                    .map(kLineDTO -> {
                        KLinePO kLine = new KLinePO();
                        BeanUtils.copyProperties(kLineDTO, kLine);
                        kLine.setCreateTime(LocalDateTime.now());
                        kLine.setUpdateTime(LocalDateTime.now());
                        return kLine;
                    })
                    .collect(Collectors.toList());
            klineService.batchInsert(List.of(crawlerTaskPO), kLinePOList);
        }
    }

}