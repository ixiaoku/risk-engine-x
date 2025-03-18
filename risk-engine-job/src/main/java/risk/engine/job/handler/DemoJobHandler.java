package risk.engine.job.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @Author: X
 * @Date: 2025/3/18 17:48
 * @Version: 1.0
 */
@Component
public class DemoJobHandler {

    @XxlJob("demoJobHandler")
    public void execute() {
        System.out.println("Demo job executed successfully!");
        String param = XxlJobHelper.getJobParam(); // 获取任务参数
        XxlJobHelper.log("This is a demo job, param: " + param);
    }
}
