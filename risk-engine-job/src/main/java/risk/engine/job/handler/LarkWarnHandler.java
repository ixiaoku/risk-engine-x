package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.job.task.AlarmRecordTask;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/4/22 23:48
 * @Version: 1.0
 */
@Slf4j
@Component
public class LarkWarnHandler {

    @Resource
    private AlarmRecordTask alarmRecordTask;

    @XxlJob("larkWarnTaskJob")
    public void larkWarnTask() {
        try {
            String param = XxlJobHelper.getJobParam();
            alarmRecordTask.execute();
            XxlJobHelper.log("larkWarnTaskJob, param: " + param);
            log.info("larkWarnTaskJob executed successfully!");
        } catch (Exception e) {
            log.error("larkWarnTaskJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("larkWarnTaskJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
