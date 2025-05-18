package risk.engine.flink.sink;

/**
 * @Author: X
 * @Date: 2025/5/17 21:03
 * @Version: 1.0
 */

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.util.List;
import java.util.Properties;

public class TestProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "43.163.107.28:9093");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("incident_code", "TradeQuantData");
        jsonObject.put("uid", "BTCUSDT");
        jsonObject.put("metric_codes", List.of("close-price-btcusdt-sum", "close-price-btcusdt-avg"));

        for (int i = 0; i < 10; i++) {
            try {
                long timestamp = System.currentTimeMillis() + i * 1000; // 10 秒前开始
                jsonObject.put("attributes", new JSONObject()
                        .put("close", i * 1000)
                        .put("timestamp", timestamp));
                String message = jsonObject.toString();
                producer.send(new ProducerRecord<>("risk_feature_events", message));
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        producer.close();
    }
}
