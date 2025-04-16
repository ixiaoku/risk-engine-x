package risk.engine.job.handler;

import com.google.common.collect.Lists;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.service.service.ICrawlerTaskService;
import risk.engine.service.service.IEngineExecuteService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/3 22:31
 * @Version: 1.0
 */
@Slf4j
@Component
public class ExecutorEngineHandler {

    @Resource
    private IEngineExecuteService engineExecuteService;

    @Resource
    private ICrawlerTaskService crawlerTaskService;

    @XxlJob("executorEngineJob")
    public void executorEngine() {
        try {
            String param = XxlJobHelper.getJobParam();
            CrawlerTaskPO crawlerTaskQuery = new CrawlerTaskPO();
            crawlerTaskQuery.setStatus(BusinessConstant.STATUS);
            List<CrawlerTaskPO> crawlerTasks = crawlerTaskService.selectByExample(crawlerTaskQuery);
            if (CollectionUtils.isEmpty(crawlerTasks)) {
                return;
            }
            Lists.partition(crawlerTasks, 100)
                    .forEach(taskList -> taskList.forEach(task -> {
                        RiskEngineParam engineParam = new RiskEngineParam();
                        engineParam.setRequestPayload(task.getRequestPayload());
                        engineParam.setIncidentCode(task.getIncidentCode());
                        engineParam.setFlowNo(task.getFlowNo());
                        engineExecuteService.execute(engineParam);
                        task.setStatus(1);
                        task.setRetry(0);
                        task.setUpdateTime(LocalDateTime.now());
                        crawlerTaskService.updateByPrimaryKey(task);
                    }));
            XxlJobHelper.log("executorEngineJob, param: " + param);
            log.info("executorEngineJob executed successfully!");
        } catch (Exception e) {
            log.error("executorEngineJob executed failed : {}", e.getMessage(), e);
            XxlJobHelper.log("executorEngineJob executed failed : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
