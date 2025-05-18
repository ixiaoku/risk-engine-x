package risk.engine.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;

import java.time.Duration;
import java.text.SimpleDateFormat;
import java.util.*;

public class SlidingWindowDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        List<Tuple2<Long, Integer>> input = Arrays.asList(
                Tuple2.of(System.currentTimeMillis(), 1),
                Tuple2.of(System.currentTimeMillis() + 2000, 2),
                Tuple2.of(System.currentTimeMillis() + 4000, 3),
                Tuple2.of(System.currentTimeMillis() + 6000, 4),
                Tuple2.of(System.currentTimeMillis() + 8000, 5),
                Tuple2.of(System.currentTimeMillis() + 10000, 6)
        );

        DataStreamSource<Tuple2<Long, Integer>> source = env.fromCollection(input);

        source
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple2<Long, Integer>>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner((event, timestamp) -> event.f0)
                )
                .keyBy(value -> 1)
                .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .process(new DebugWindowFunction())
                .print();

        env.execute("Sliding Window with Debug Output");
    }

    public static class DebugWindowFunction extends ProcessWindowFunction<
            Tuple2<Long, Integer>, // 输入类型
            String,                // 输出类型
            Integer,               // Key 类型
            TimeWindow> {

        private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

        @Override
        public void process(Integer key,
                            Context context,
                            Iterable<Tuple2<Long, Integer>> elements,
                            Collector<String> out) {
            List<Tuple2<Long, Integer>> list = new ArrayList<>();
            elements.forEach(list::add);

            long sum = list.stream().mapToLong(e -> e.f1).sum();

            StringBuilder sb = new StringBuilder();
            sb.append("\n=== 窗口计算开始 ===\n");
            sb.append("窗口时间: ").append(sdf.format(new Date(context.window().getStart())))
                    .append(" ~ ").append(sdf.format(new Date(context.window().getEnd()))).append("\n");
            sb.append("数据如下:\n");
            for (Tuple2<Long, Integer> element : list) {
                sb.append("  时间: ").append(sdf.format(new Date(element.f0)))
                        .append(", 值: ").append(element.f1).append("\n");
            }
            sb.append("窗口内总和: ").append(sum).append("\n");
            sb.append("=== 窗口计算结束 ===\n");
            out.collect(sb.toString());
        }
    }
}