package risk.engine.flink.sink;

/**
 * @Author: X
 * @Date: 2025/5/17 21:03
 * @Version: 1.0
 */

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class TestProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "43.163.107.28:9093");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        String message = "{\"incident_code\":\"TradeQuantData\",\"uid\":\"BTCUSDT\",\"attributes\":{\"close\":76662.35000000,\"timestamp\":1744026599999},\"metric_codes\":[\"close-price-btcusdt-sum\",\"close-price-btcusdt-avg\"]}";
        producer.send(new ProducerRecord<>("risk_feature_events", message));
        producer.close();
    }
}
