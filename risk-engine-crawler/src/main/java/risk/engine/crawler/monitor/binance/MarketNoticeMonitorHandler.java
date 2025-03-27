package risk.engine.crawler.monitor.binance;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.db.entity.CrawlerTask;
import risk.engine.dto.constant.CrawlerConstant;
import risk.engine.dto.dto.crawler.CrawlerNoticeDTO;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
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
    private ICrawlerTaskService crawlerTaskService;

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
        crawlerTaskService.batchInsert(crawlerTasks);
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
                String flowNo = articleObject.get("id").getAsString();
                String title = articleObject.get("title").getAsString();
                String createdAt = DateTimeUtil.getTimeByTimestamp(articleObject.get("releaseDate").getAsLong());
                CrawlerNoticeDTO noticeDTO = new CrawlerNoticeDTO();
                noticeDTO.setFlowNo(flowNo);
                noticeDTO.setTitle(title);
                noticeDTO.setCreatedAt(createdAt);
                CrawlerTask crawlerTask = crawlerTaskService.getCrawlerTask(flowNo, CrawlerConstant.BINANCE_NOTICE_LIST, JSON.toJSONString(noticeDTO));
                if (Objects.isNull(crawlerTask)) {
                    continue;
                }
                crawlerTasks.add(crawlerTask);
            }
        }
        return crawlerTasks;
    }
}