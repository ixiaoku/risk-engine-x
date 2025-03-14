package risk.engine.common.listener;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.debezium.engine.format.Json;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.data.Struct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MySQL Binlog 监听器，监控 risk.rule 和 risk.incident 表变更，实时刷新缓存
 * @Author: X
 * @Date: 2025/3/12 21:41
 * @Version: 1.0
 */
public class BinlogCacheRefresher {

    // 缓存实例，线程安全
    private static final Cache CACHE = new Cache();

    public static void main(String[] args) {
        // 配置 Debezium，连接 MySQL 并监听指定表
        Configuration config = Configuration.create()
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector") // MySQL 连接器
                .with("database.hostname", "localhost") // MySQL 主机
                .with("database.port", "3306") // MySQL 端口
                .with("database.user", "dcr") // MySQL 用户名
                .with("database.password", "dcr") // MySQL 密码
                .with("database.server.id", "1") // 唯一服务器 ID
                .with("database.include.list", "risk") // 监控的数据库
                .with("table.include.list", "risk.rule,risk.incident") // 监控的表
                .with("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore") // 内存存储偏移量
                .with("snapshot.mode", "initial") // 初始快照模式，加载现有数据
                .build();

        // 创建 Debezium 引擎，使用 SourceRecord 格式
//        DebeziumEngine<RecordChangeEvent<SourceRecord>> engine = DebeziumEngine.create(ChangeEventFormat.of(Json.class))
//                .using(config)
//                .notifying(new CacheUpdateHandler(CACHE))
//                .build();

        // 使用线程池运行引擎，避免阻塞主线程
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            //engine.run();
            System.out.println("Debezium 引擎启动完成，开始监听 binlog");
        });

        // 模拟接口调用，展示缓存使用
        simulateApiCall();
    }

    // 模拟高性能接口调用，从缓存读取数据
    private static void simulateApiCall() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 每秒模拟一次调用
                    Map<String, Object> rule = CACHE.getRule(1); // 示例：查询 rule ID 为 1 的记录
                    System.out.println("接口查询缓存 - Rule ID 1: " + rule);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

/**
 * 缓存类，使用 ConcurrentHashMap 存储 risk.rule 和 risk.incident 数据
 */
class Cache {
    // 缓存 risk.rule 表，主键为 Integer，值为整行数据
    private final ConcurrentHashMap<Integer, Map<String, Object>> ruleCache = new ConcurrentHashMap<>();
    // 缓存 risk.incident 表，主键为 Integer，值为整行数据
    private final ConcurrentHashMap<Integer, Map<String, Object>> incidentCache = new ConcurrentHashMap<>();

    // 插入或更新 rule 记录
    public void upsertRule(int id, Map<String, Object> row) {
        ruleCache.put(id, row);
        System.out.println("更新缓存 - Rule ID: " + id + ", 数据: " + row);
    }

    // 删除 rule 记录
    public void deleteRule(int id) {
        ruleCache.remove(id);
        System.out.println("删除缓存 - Rule ID: " + id);
    }

    // 插入或更新 incident 记录
    public void upsertIncident(int id, Map<String, Object> row) {
        incidentCache.put(id, row);
        System.out.println("更新缓存 - Incident ID: " + id + ", 数据: " + row);
    }

    // 删除 incident 记录
    public void deleteIncident(int id) {
        incidentCache.remove(id);
        System.out.println("删除缓存 - Incident ID: " + id);
    }

    // 查询 rule 缓存，供接口使用
    public Map<String, Object> getRule(int id) {
        return ruleCache.get(id);
    }

    // 查询 incident 缓存，供接口使用
    public Map<String, Object> getIncident(int id) {
        return incidentCache.get(id);
    }
}

/**
 * 处理 binlog 变更事件，刷新缓存
 */
class CacheUpdateHandler implements DebeziumEngine.ChangeConsumer<RecordChangeEvent<SourceRecord>> {
    private final Cache cache;

    public CacheUpdateHandler(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void handleBatch(List<RecordChangeEvent<SourceRecord>> records, DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer) throws InterruptedException {
        for (RecordChangeEvent<SourceRecord> record : records) {
            SourceRecord sourceRecord = record.record();
            Struct value = (Struct) sourceRecord.value();
            if (value == null) continue; // 忽略空值

            // 获取操作类型：c=INSERT, u=UPDATE, d=DELETE
            String op = value.getString("op");
            // 获取表名，从 topic 中提取
            String table = sourceRecord.topic().replace("risk.", "");
            // 提取主键（假设为 id，类型为 Integer）
            Struct key = (Struct) sourceRecord.key();
            Integer id = key != null ? key.getInt32("id") : null;
            if (id == null) continue; // 跳过无主键记录

            // 根据表名处理缓存
            if ("rule".equals(table)) {
                handleRuleChange(op, id, value);
            } else if ("incident".equals(table)) {
                handleIncidentChange(op, id, value);
            }

            // 提交偏移量，确保事件处理完成
            committer.markProcessed(record);
        }
        committer.markBatchFinished();
    }

    // 处理 risk.rule 表变更
    private void handleRuleChange(String op, Integer id, Struct value) {
        switch (op) {
            case "c": // INSERT
            case "u": // UPDATE
                Map<String, Object> after = extractRow(value.getStruct("after"));
                if (after != null) cache.upsertRule(id, after);
                break;
            case "d": // DELETE
                cache.deleteRule(id);
                break;
            default:
                System.out.println("未知操作: " + op);
        }
    }

    // 处理 risk.incident 表变更
    private void handleIncidentChange(String op, Integer id, Struct value) {
        switch (op) {
            case "c": // INSERT
            case "u": // UPDATE
                Map<String, Object> after = extractRow(value.getStruct("after"));
                if (after != null) cache.upsertIncident(id, after);
                break;
            case "d": // DELETE
                cache.deleteIncident(id);
                break;
            default:
                System.out.println("未知操作: " + op);
        }
    }

    // 从 Struct 提取行数据
    private Map<String, Object> extractRow(Struct rowStruct) {
        if (rowStruct == null) return null;
        Map<String, Object> row = new HashMap<>();
        for (org.apache.kafka.connect.data.Field field : rowStruct.schema().fields()) {
            row.put(field.name(), rowStruct.get(field));
        }
        return row;
    }
}