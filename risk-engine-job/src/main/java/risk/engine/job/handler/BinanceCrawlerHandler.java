package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.crawler.monitor.binance.MarketNoticeMonitorHandler;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/18 17:48
 * @Version: 1.0
 */
@Slf4j
@Component
public class BinanceCrawlerHandler {

    @Resource
    private MarketNoticeMonitorHandler noticeMonitorHandler;

    @XxlJob("newMarketNoticeJob")
    public void executeMarketNoticeNew() {
        String param = XxlJobHelper.getJobParam();
        noticeMonitorHandler.start();
        XxlJobHelper.log("binanceNoticeJob, param: " + param);
        log.info("binanceNoticeJob job executed successfully!");
    }
}
