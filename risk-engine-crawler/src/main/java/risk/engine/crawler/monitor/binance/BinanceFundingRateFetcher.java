package risk.engine.crawler.monitor.binance;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.FundingRateDTO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author: X
 * @Date: 2025/4/25 16:53
 * @Version: 1.0
 */
@Component
public class BinanceFundingRateFetcher {

    @Resource
    private ICrawlerTaskService crawlerTaskService;

    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplate;

    private static final String BASE_URL = "https://fapi.binance.com";

    public void start() {
        String url = BASE_URL + "/fapi/v1/premiumIndex";
        String result = OkHttpUtil.get(url);
        if (Objects.isNull(result)) return;
        List<FundingRateDTO> fundingRateDTOS = JSON.parseArray(result, FundingRateDTO.class);
        if (CollectionUtils.isEmpty(fundingRateDTOS)) return;
        List<CrawlerTaskPO> crawlerTaskPOS = new ArrayList<>();
        for (FundingRateDTO fundingRateDTO : fundingRateDTOS) {
            AnnouncementDTO announcementDTO = new AnnouncementDTO();
            announcementDTO.setCreatedAt(DateTimeUtil.getTimeByTimestamp(fundingRateDTO.getTime()));
            String lastFundingRate = fundingRateDTO.getLastFundingRate().multiply(new BigDecimal(100)) + "%";
            String content = String.format(CrawlerConstant.CONTRACT_FUNDING_RATE_CONTENT, fundingRateDTO.getSymbol(), lastFundingRate, fundingRateDTO.getMarkPrice(), fundingRateDTO.getIndexPrice(), DateTimeUtil.getTimeByTimestamp(fundingRateDTO.getNextFundingTime()));
            announcementDTO.setContent(content);
            fundingRateDTO.setAnnouncement(announcementDTO);
            String flowNo = fundingRateDTO.getSymbol() + fundingRateDTO.getTime();
            CrawlerTaskPO crawlerTaskPO = crawlerTaskService.getCrawlerTask(flowNo, IncidentCodeEnum.CONTRACT_FUNDING_RATE.getCode(), JSON.toJSONString(fundingRateDTO));
            if (Objects.isNull(crawlerTaskPO)) continue;
            crawlerTaskPOS.add(crawlerTaskPO);
        }
        crawlerTaskService.batchInsert(crawlerTaskPOS);
    }

    private void setContractSymbol() {
        String url = BASE_URL + "/fapi/v1/exchangeInfo";
        String result = OkHttpUtil.get(url);
        if (Objects.isNull(result)) {
            return;
        }
        JSONObject obj = JSON.parseObject(result);
        JSONArray symbols = obj.getJSONArray("symbols");
        Set<String> symbolSet = new HashSet<>();
        for (int i = 0; i < symbols.size(); i++) {
            JSONObject jsonObject = symbols.getJSONObject(i);
            String symbol = jsonObject.getString("symbol");
            symbolSet.add(symbol);
        }
        if (CollectionUtils.isEmpty(symbolSet)) {
            return;
        }
        redisTemplate.opsForSet().add("ContractSymbol:", symbolSet.toArray(new String[0]));
    }

}
