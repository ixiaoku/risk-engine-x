package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.crawler.monitor.binance.MarketNoticeMonitorHandler;
import risk.engine.crawler.monitor.transfer.BinanceKlineFetcher;
import risk.engine.crawler.monitor.twitter.TwitterCrawlerUserHandler;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/28 10:24
 * @Version: 1.0
 */
@Slf4j
@Component
public class CrawlerDataJobHandler {

    @Resource
    private TwitterCrawlerUserHandler crawlerUserHandler;

    @Resource
    private MarketNoticeMonitorHandler marketNoticeMonitorHandler;

    @Resource
    private BinanceKlineFetcher binanceKlineFetcher;

    @XxlJob("crawlerTwitterUserJob")
    public void crawlerTwitterUser() {
        try {
            String param = XxlJobHelper.getJobParam();
            crawlerUserHandler.start();
            XxlJobHelper.log("crawlerTwitterUserJob, param: " + param);
            log.info("crawlerTwitterUserJob executed successfully!");
        } catch (Exception e) {
            log.error("crawlerTwitterUserJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("crawlerTwitterUserJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @XxlJob("crawlerBinanceNoticeJob")
    public void crawlerBinanceNotice() {
        try {
            String param = XxlJobHelper.getJobParam();
            marketNoticeMonitorHandler.start();
            XxlJobHelper.log("crawlerBinanceNoticeJob, param: " + param);
            log.info("crawlerBinanceNoticeJob executed successfully!");
        } catch (Exception e) {
            log.error("crawlerBinanceNoticeJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("crawlerBinanceNoticeJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @XxlJob("tradeQuantDataJob")
    public void tradeQuantData() {
        try {
            String param = XxlJobHelper.getJobParam();
            binanceKlineFetcher.start();
            XxlJobHelper.log("tradeQuantDataJob, param: " + param);
            log.info("tradeQuantDataJob executed successfully!");
        } catch (Exception e) {
            log.error("tradeQuantDataJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("tradeQuantDataJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
