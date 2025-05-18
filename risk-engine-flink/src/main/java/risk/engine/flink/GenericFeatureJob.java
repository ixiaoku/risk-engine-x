package risk.engine.flink;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import risk.engine.flink.model.*;
import risk.engine.flink.sink.RedisSink;
import risk.engine.flink.util.ConfigLoader;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: X
 * @Date: 2025/3/14 16:55
 * @Version: 1.0
 */
@Slf4j
public class GenericFeatureJob {
    private static final ConfigLoader configLoader = new ConfigLoader();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        env.enableCheckpointing(60000);
        env.getCheckpointConfig().setCheckpointStorage("file:///Users/dongchunrong/Documents/data/flink/checkpoints");
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("43.163.107.28:9093")
                .setTopics("risk_feature_events")
                .setGroupId("flink-feature-consumer")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<FeatureEvent> eventStream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "KafkaSource")
                .map(json -> {
                    try {
                        return mapper.readValue(json, FeatureEvent.class);
                    } catch (Exception e) {
                        log.error("Failed to parse JSON: {}", json, e);
                        return null;
                    }
                })
                .filter(e -> Objects.nonNull(e) && e.getUid() != null && e.getMetricCodes() != null)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<FeatureEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner((event, timestamp) -> {
                                    Object timestampObj = event.getAttributes().get("timestamp");
                                    if (timestampObj instanceof Number) {
                                        timestamp = ((Number) timestampObj).longValue();
                                    } else if (timestampObj instanceof String) {
                                        try {
                                            timestamp = Long.parseLong((String) timestampObj);
                                        } catch (Exception e) {
                                            log.error("Failed to parse timestamp from string: {}", timestampObj);
                                        }
                                    }
                                    return timestamp;
                                })
                );

        DataStream<FeatureResult> resultStream = eventStream
                .flatMap((event, collector) -> {
                    List<CounterMetricConfig> configs = configLoader.getConfigsByMetricCodes(event.getMetricCodes());
                    if (CollectionUtils.isEmpty(configs)) {
                        log.warn("No configs found for metricCodes: {}", event.getMetricCodes());
                    }
                    for (CounterMetricConfig config : configs) {
                        if (!config.getIncidentCode().equals(event.getIncidentCode())) {
                            log.info("Skipping config with mismatched incidentCode: {}", config);
                            continue;
                        }
                        Object attributeValue = event.getAttributes().get(config.getAttributeKey());
                        double value;
                        if (config.getAggregationType().equalsIgnoreCase("counter")) {
                            value = 1.0;
                        } else if (attributeValue instanceof Number) {
                            value = ((Number) attributeValue).doubleValue();
                        } else {
                            log.warn("Invalid attribute value for key {}: {}", config.getAttributeKey(), attributeValue);
                            value = 0.0;
                        }
                        collector.collect(new IntermediateResult(
                                config.getMetricCode(),
                                event.getUid(),
                                value,
                                getWindowSizeSeconds(config.getWindowSize()),
                                config.getAggregationType()
                        ));
                    }
                }, TypeInformation.of(new TypeHint<IntermediateResult>() {}))
                .keyBy(result -> result.getUid() + ":" + result.getMetricCode())
                //.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .aggregate(new FeatureAggregator())
                .map(result -> {
                    log.info("输出前的特征值：{}", result);
                    return result;
                });

        resultStream.addSink(new RedisSink("43.163.107.28", 6379, "dcr"));
        log.info("Flink job started successfully");
        env.execute("Generic Feature Computation Job");
    }

    private static long getWindowSizeSeconds(String windowSize) {
        switch (windowSize) {
            case "10s": return 10;
            case "1min": return TimeUnit.MINUTES.toSeconds(1);
            case "5min": return TimeUnit.MINUTES.toSeconds(5);
            case "15min": return TimeUnit.MINUTES.toSeconds(15);
            case "30min": return TimeUnit.MINUTES.toSeconds(30);
            case "1h": return TimeUnit.HOURS.toSeconds(1);
            case "2h": return TimeUnit.HOURS.toSeconds(2);
            case "4h": return TimeUnit.HOURS.toSeconds(4);
            case "12h": return TimeUnit.HOURS.toSeconds(12);
            default: return TimeUnit.HOURS.toSeconds(24);
        }
    }
}
