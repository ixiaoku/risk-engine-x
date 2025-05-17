package risk.engine.flink.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;

/**
 * @Author: X
 * @Date: 2025/5/18 02:04
 * @Version: 1.0
 */
@Slf4j
public class FeatureWindowFunction extends ProcessWindowFunction<FeatureResult, FeatureResult, String, TimeWindow> {
    @Override
    public void process(String key, Context context, Iterable<FeatureResult> elements, Collector<FeatureResult> out) throws Exception {
        for (FeatureResult result : elements) {
            log.info("【窗口输出】key={}, window=[{} ~ {}], result={}",
                    key,
                    Instant.ofEpochMilli(context.window().getStart()),
                    Instant.ofEpochMilli(context.window().getEnd()),
                    result);

            out.collect(result); // 传给 downstream sink，如 RedisSink
        }
    }
}
