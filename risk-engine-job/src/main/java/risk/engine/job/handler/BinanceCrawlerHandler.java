package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: X
 * @Date: 2025/3/18 17:48
 * @Version: 1.0
 */
@Slf4j
@Component
public class BinanceCrawlerHandler {

    @XxlJob("marketNoticeNewJob")
    public void executeMarketNoticeNew() {
        String param = XxlJobHelper.getJobParam(); // 获取任务参数
        XxlJobHelper.log("binanceNoticeJob, param: " + param);
        log.info("binanceNoticeJob job executed successfully!");
    }
}
