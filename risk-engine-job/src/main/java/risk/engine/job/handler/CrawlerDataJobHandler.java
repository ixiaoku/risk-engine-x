package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.crawler.monitor.binance.BinanceFundingRateFetcher;
import risk.engine.crawler.monitor.binance.BinanceKlineFetcher;
import risk.engine.crawler.monitor.binance.BinancePriceFetcher;
import risk.engine.crawler.monitor.binance.MarketNoticeMonitorHandler;
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

    @Resource
    private BinancePriceFetcher binancePriceFetcher;

    @Resource
    private BinanceFundingRateFetcher fundingRateFetcher;

    //@XxlJob("crawlerTwitterUserJob")
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

    //@XxlJob("crawlerBinanceNoticeJob")
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

    @XxlJob("windowPriceDataJob")
    public void windowPriceData() {
        try {
            String param = XxlJobHelper.getJobParam();
            binancePriceFetcher.start();
            XxlJobHelper.log("windowPriceDataJob, param: " + param);
            log.info("windowPriceDataJob executed successfully!");
        } catch (Exception e) {
            log.error("windowPriceDataJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("windowPriceDataJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @XxlJob("fundingRateDataJob")
    public void fundingRateData() {
        try {
            String param = XxlJobHelper.getJobParam();
            fundingRateFetcher.start();
            XxlJobHelper.log("fundingRateDataJob, param: " + param);
            log.info("fundingRateDataJob executed successfully!");
        } catch (Exception e) {
            log.error("fundingRateDataJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("fundingRateDataJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
