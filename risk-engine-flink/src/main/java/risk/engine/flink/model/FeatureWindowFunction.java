package risk.engine.flink.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/5/18 02:04
 * @Version: 1.0
 */
@Slf4j
public class FeatureWindowFunction extends ProcessWindowFunction<FeatureResult, FeatureResult, String, TimeWindow> {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    @Override
    public void process(String key, Context context, Iterable<FeatureResult> elements, Collector<FeatureResult> out) {
        List<FeatureResult> resultList = new ArrayList<>();
        elements.forEach(resultList::add);

        log.info("\n=== 窗口计算开始 ===\nkey: {}\n窗口时间: {} ~ {}\n共 {} 条数据",
                key,
                TIME_FORMATTER.format(Instant.ofEpochMilli(context.window().getStart())),
                TIME_FORMATTER.format(Instant.ofEpochMilli(context.window().getEnd())),
                resultList.size());

        for (FeatureResult result : resultList) {
            log.info("  uid={}, metricCode={}, value={}, count={}, aggregationType={}",
                    result.getUid(),
                    result.getMetricCode(),
                    result.getValue(),
                    result.getCount(),
                    result.getAggregationType());
        }

        FeatureResult merged = mergeResults(resultList);
        log.info("窗口聚合结果: {}", merged);
        log.info("=== 窗口计算结束 ===\n");

        out.collect(merged);
    }

    private FeatureResult mergeResults(List<FeatureResult> results) {
        if (results.isEmpty()) return null;
        FeatureResult first = results.get(0);

        double valueSum = 0.0;
        long countSum = 0;

        for (FeatureResult r : results) {
            valueSum += r.getValue();
            countSum += r.getCount();
        }

        return new FeatureResult(
                first.getMetricCode(),
                first.getUid(),
                valueSum,
                countSum,
                first.getWindowSizeSeconds(),
                first.getAggregationType()
        );
    }
}