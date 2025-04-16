package risk.engine.job.consume;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Author: X
 * @Date: 2025/4/12 14:28
 * @Version: 1.0
 */
@Slf4j
@Component
public class CanalKafkaListener {

    @KafkaListener(topics = "risk_binlog_topic", groupId = "")
    public void handleCanalMessage(String message) {
        log.info("Received binlog: {}", message);
        JSONObject json = JSON.parseObject(message);

        String table = json.getString("table");
        JSONArray data = json.getJSONArray("data");

        if (data == null || data.isEmpty()) return;

        for (int i = 0; i < data.size(); i++) {
            JSONObject row = data.getJSONObject(i);
            String key = "risk:" + table + ":" + row.getString("id");
            log.info("canal监听的数据 {}" ,row);
        }
    }

}
