package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

    @XxlJob("crawlerTwitterUserJob")
    public void crawlerTwitterUser() {
        try {
            String param = XxlJobHelper.getJobParam();
            crawlerUserHandler.start();
            XxlJobHelper.log("penaltyExecuteTaskJob, param: " + param);
            log.info("crawlerTwitterUserJob executed successfully!");
        } catch (Exception e) {
            log.error("crawlerTwitterUserJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("crawlerTwitterUserJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
