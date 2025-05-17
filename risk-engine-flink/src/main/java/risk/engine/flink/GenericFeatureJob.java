package risk.engine.flink;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GenericFeatureJob {
    private static final Logger LOG = LoggerFactory.getLogger(GenericFeatureJob.class);
    private static final ConfigLoader configLoader = new ConfigLoader();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        env.enableCheckpointing(60000);
        env.getCheckpointConfig().setCheckpointStorage("file:///Users/dongchunrong/Documents/data/flink/checkpoints");
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10000));
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
                        LOG.error("Failed to parse JSON: {}", json, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<FeatureEvent>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                                .withTimestampAssigner((event, timestamp) -> (Long) event.getAttributes().get("timestamp"))
                );

        DataStream<FeatureResult> resultStream = eventStream
                .flatMap((event, collector) -> {
                    List<CounterMetricConfig> configs = configLoader.getConfigsByMetricCodes(event.getMetricCodes());
                    for (CounterMetricConfig config : configs) {
                        if (!config.getIncidentCode().equals(event.getIncidentCode())) {
                            continue;
                        }
                        Object attributeValue = event.getAttributes().get(config.getAttributeKey());
                        double value;
                        if (config.getAggregationType().equals("counter")) {
                            value = 1.0;
                        } else if (attributeValue instanceof Number) {
                            value = ((Number) attributeValue).doubleValue();
                        } else {
                            LOG.warn("Invalid attribute value for key {}: {}", config.getAttributeKey(), attributeValue);
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
                .window(SlidingEventTimeWindows.of(Time.hours(24), Time.minutes(5))) // 需改进为动态窗口
                .aggregate(new FeatureAggregator())
                .map(result -> new FeatureResult(
                        result.getMetricCode(),
                        result.getUid(),
                        result.getValue(),
                        result.getWindowSizeSeconds()
                ));
        resultStream.addSink(new RedisSink("redis", 6379, "dcr"));
        resultStream.addSink(new RedisSink("redis", 6379, "dcr"));
        env.execute("Generic Feature Computation Job");
    }

    private static long getWindowSizeSeconds(String windowSize) {
        switch (windowSize) {
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

