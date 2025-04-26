package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.job.task.QueueFailedTask;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/4/22 23:48
 * @Version: 1.0
 */
@Slf4j
@Component
public class QueueFailedJobHandler {

    @Resource
    private QueueFailedTask queueFailedTask;

    @XxlJob("queueFailedTaskJob")
    public void queueFailedTask() {
        try {
            String param = XxlJobHelper.getJobParam();
            queueFailedTask.execute();
            XxlJobHelper.log("queueFailedTaskJob, param: " + param);
            log.info("queueFailedTaskJob executed successfully!");
        } catch (Exception e) {
            log.error("queueFailedTaskJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("queueFailedTaskJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
