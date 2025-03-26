package risk.engine.crawler.monitor.binance;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.db.dao.CrawlerTaskMapper;
import risk.engine.db.entity.CrawlerTask;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.enums.TaskStatusEnum;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 币安公告监控
 */
@Slf4j
@Component
public class MarketNoticeMonitorHandler {

    @Resource
    private CrawlerTaskMapper crawlerTaskMapper;

    public void start() {
        String jsonResponse = OkHttpUtil.get(CrawlerConstant.notIceUrl);
        if (StringUtils.isEmpty(jsonResponse)) {
            log.error("请求失败，未获取到数据");
            return;
        }
        // 解析json 获取公告爬虫数据
        List<CrawlerTask> crawlerTasks = getCrawlerTasks(jsonResponse);
        if (CollectionUtils.isEmpty(crawlerTasks)) {
            return;
        }
        //批量保存爬虫数据
        crawlerTaskMapper.batchInsert(crawlerTasks);
    }

    /**
     * 解析json报文 获取公告爬虫数据
     * @param jsonResponse 参数
     * @return 结果
     */
    private List<CrawlerTask> getCrawlerTasks(String jsonResponse) {
        JsonObject rootObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        // 检查返回状态
        String code = rootObject.get("code").getAsString();
        if (!CrawlerConstant.notIceCode.equals(code)) {
            log.error("API 返回错误，code: {}, message: {}", code, rootObject.get("message"));
            return List.of();
        }
        JsonObject data = rootObject.get("data").getAsJsonObject();
        // 获取 data 数组
        JsonArray catalogs = data.getAsJsonArray("catalogs");
        if (Objects.isNull(catalogs)) {
            log.error("未找到 data 数组");
        }
        List<CrawlerTask> crawlerTasks = new ArrayList<>();
        for (JsonElement element : catalogs) {
            JsonObject dataObject = element.getAsJsonObject();
            JsonArray articles =  dataObject.get("articles").getAsJsonArray();
            if (Objects.isNull(articles)) {
                continue;
            }
            for (JsonElement article : articles) {
                JsonObject articleObject = article.getAsJsonObject();
                String articleCode = articleObject.get("code").getAsString();
                //去重 重复的不保存
                CrawlerTask taskQuery = new CrawlerTask();
                taskQuery.setFlowNo(articleCode);
                taskQuery.setIncidentCode(CrawlerConstant.BINANCE_NOTICE_LIST);
                List<CrawlerTask> crawlerTaskList = crawlerTaskMapper.selectByExample(taskQuery);
                if (CollectionUtils.isNotEmpty(crawlerTaskList)) {
                    continue;
                }
                //组装爬虫数据
                CrawlerTask crawlerTask = new CrawlerTask();
                crawlerTask.setFlowNo(articleCode);
                crawlerTask.setIncidentCode(CrawlerConstant.BINANCE_NOTICE_LIST);
                crawlerTask.setStatus(TaskStatusEnum.WAIT.getCode());
                crawlerTask.setRetry(BusinessConstant.RETRY);
                crawlerTask.setRequestPayload(articleObject.getAsString());
                crawlerTask.setCreateTime(LocalDateTime.now());
                crawlerTask.setUpdateTime(LocalDateTime.now());
                crawlerTasks.add(crawlerTask);
            }
        }
        return crawlerTasks;
    }
}