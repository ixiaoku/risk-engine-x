package risk.engine.crawler.monitor.transfer;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.dto.crawler.BewNewsDTO;
import risk.engine.dto.dto.penalty.AnnouncementDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
import risk.engine.service.service.IAlarmRecordService;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/24 23:02
 * @Version: 1.0
 */
@Slf4j
@Component
public class BweNewsWebSocketClient extends WebSocketClient implements ApplicationRunner {

    @Resource
    private ICrawlerTaskService crawlerTaskService;

    @Resource
    private IAlarmRecordService alarmRecordService;

    public BweNewsWebSocketClient() throws URISyntaxException {
        super(new URI("wss://bwenews-api.bwe-ws.com/ws"));;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Connected to BWEnews WebSocket.");
    }

    @Override
    public void onMessage(String message) {
        String title = "";
        try {
            JSONObject json = new JSONObject(message);
            String source = json.optString("source_name");
            title = json.optString("news_title");
            String url = json.optString("url");
            long timestamp = json.optLong("timestamp");
            AnnouncementDTO announcementDTO = new AnnouncementDTO();
            announcementDTO.setTitle(null);
            announcementDTO.setContent(title);
            announcementDTO.setCreatedAt(DateTimeUtil.getTimeByTimestamp(timestamp*1000));
            BewNewsDTO bewNewsDTO = new BewNewsDTO();
            bewNewsDTO.setTitle(title);
            bewNewsDTO.setTimestamp(timestamp);
            bewNewsDTO.setUrl(url);
            bewNewsDTO.setSourceName(source);
            bewNewsDTO.setAnnouncement(announcementDTO);
            String flowNo = source + timestamp;
            CrawlerTaskPO crawlerTaskPO = crawlerTaskService.getCrawlerTask(flowNo, IncidentCodeEnum.BEWNEWS.getCode(), JSON.toJSONString(bewNewsDTO));
            if (crawlerTaskPO != null) {
                log.info("BewNews start saved");
                crawlerTaskService.batchInsert(List.of(crawlerTaskPO));
            }
        } catch (Exception e) {
            log.error("Error parsing message: {}", message, e);
            alarmRecordService.insertAsync("BewNews新闻获取失败：" + title, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.error("Connection closed. Reason: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket Error: {}", ex.getMessage());
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("BWEnews WebSocket started.");
        this.connect();
    }
}
