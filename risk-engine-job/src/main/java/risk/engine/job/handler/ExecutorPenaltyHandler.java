package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.job.task.PenaltyExecuteTask;

import javax.annotation.Resource;

/**
 * 处罚执行任务
 * @Author: X
 * @Date: 2025/3/18 17:48
 * @Version: 1.0
 */
@Slf4j
@Component
public class ExecutorPenaltyHandler {

    @Resource
    private PenaltyExecuteTask penaltyExecuteTask;

    @XxlJob("penaltyExecuteTaskJob")
    public void penaltyExecuteTask() {
        try {
            String param = XxlJobHelper.getJobParam();
            penaltyExecuteTask.execute();
            XxlJobHelper.log("binanceNoticeJob, param: " + param);
            log.info("penaltyExecuteTaskJob executed successfully!");
        } catch (Exception e) {
            log.error("penaltyExecuteTaskJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("penaltyExecuteTaskJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
