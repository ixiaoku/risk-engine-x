package risk.engine.crawler.monitor.binance;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.MarketTickerDTO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/4/15 12:20
 * @Version: 1.0
 */
@Slf4j
@Component
public class BinancePriceFetcher {

    private static final String BINANCE_TICKER_URL = "https://api.binance.com/api/v3/ticker";

    private static final String BINANCE_EXCHANGE_INFO_URL = "https://api.binance.com/api/v3/exchangeInfo";

    private static final String REDIS_KEY = "binance:usdt-symbols";
    @Resource
    private ICrawlerTaskService crawlerTaskService;

    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplate;

    public void start() {
        Set<String> symbolSet = redisTemplate.opsForSet().members(REDIS_KEY);
        if (CollectionUtils.isEmpty(symbolSet)) {
            Set<String> symbolSets = fetchSymbols();
            String[] symbolArray = symbolSets.toArray(new String[0]);
            redisTemplate.opsForSet().add(REDIS_KEY, symbolArray);
            return;
        }
        List<CrawlerTaskPO> crawlerTaskPOList = fetchPrices(symbolSet);
        if (CollectionUtils.isEmpty(crawlerTaskPOList)) return;
        log.info("crawlerTaskPO 保存成功; size: {}", crawlerTaskPOList.size());
        crawlerTaskService.batchInsert(crawlerTaskPOList);
    }

    /**
     * 请求 Binance 所有价格信息，筛选出 USDT 交易对的价格
     */
    private List<CrawlerTaskPO> fetchPrices(Set<String> symbolSet) {
        List<String> symbolList = symbolSet.stream().map(Object::toString).collect(Collectors.toList());
        List<List<String>> symbols = Lists.partition(symbolList, 100);
        List<CrawlerTaskPO> crawlerList = Lists.newArrayList();
        for (List<String> list : symbols) {
            String symbolsJson = JSON.toJSONString(list);
            String url = BINANCE_TICKER_URL + "?symbols=" + symbolsJson + "&windowSize=5m&type=FULL";
            String json = OkHttpUtil.get(url);
            List<MarketTickerDTO> marketTickerDTOList = JSON.parseArray(json, MarketTickerDTO.class);
            if (CollectionUtils.isEmpty(marketTickerDTOList)) {
                continue;
            }
            List<CrawlerTaskPO> crawlerTaskPOList = marketTickerDTOList.stream().map(m -> {
                boolean flag = m.getPriceChangePercent().compareTo(BigDecimal.ZERO) > 0;
                if (flag) {
                    m.setUpPriceChangePercent(m.getPriceChangePercent());
                    m.setDownPriceChangePercent(BigDecimal.ZERO);
                } else {
                    m.setUpPriceChangePercent(BigDecimal.ZERO);
                    m.setDownPriceChangePercent(m.getPriceChangePercent());
                }
                String flowNo = m.getSymbol() + ":" + m.getOpenTime();
                AnnouncementDTO announcementDTO = new AnnouncementDTO();
                announcementDTO.setCreatedAt(DateTimeUtil.getTimeByTimestamp(m.getOpenTime()));
                String content = String.format(CrawlerConstant.WINDOW_PRICE_CHANGE_STATISTICS_CONTENT, m.getSymbol(), m.getPriceChange(), m.getPriceChangePercent() + "%", m.getVolume(), m.getLastPrice());
                announcementDTO.setContent(content);
                m.setAnnouncement(announcementDTO);
                return crawlerTaskService.getCrawlerTask(flowNo, IncidentCodeEnum.WINDOW_PRICE_CHANGE_STATISTICS.getCode(),
                        JSON.toJSONString(m));
            }).collect(Collectors.toList());
            crawlerList.addAll(crawlerTaskPOList);
        }
        return crawlerList;
    }

    private Set<String> fetchSymbols() {
        Set<String> usdtSymbols = new HashSet<>();
        try {
            String jsonData = OkHttpUtil.get(BINANCE_EXCHANGE_INFO_URL);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonData);
            JsonNode symbols = root.path("symbols");
            for (JsonNode symbolNode : symbols) {
                String symbol = symbolNode.path("symbol").asText();
                String quoteAsset = symbolNode.path("quoteAsset").asText();
                String status = symbolNode.path("status").asText();
                if ("USDT".equals(quoteAsset) && "TRADING".equals(status)) {
                    usdtSymbols.add(symbol);
                }
            }
        } catch (Exception e) {
            log.error("获取币对信息：{}",e.getMessage(), e);
        }
        return usdtSymbols;
    }

}
