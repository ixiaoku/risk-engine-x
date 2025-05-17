package risk.engine.flink.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import risk.engine.flink.model.CounterMetricConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/5/17 18:04
 * @Version: 1.0
 */
@Slf4j
public class ConfigLoader {
    private static final String MYSQL_URL = "jdbc:mysql://43.163.107.28:3306/risk?useSSL=false";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "dcr";

    private static final String REDIS_HOST = "43.163.107.28";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = "dcr";
    private static final int REDIS_TIMEOUT = 2000;

    private static final String CONFIG_KEY_PREFIX = "counter_metric_config:";

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, CounterMetricConfig> configCache = new ConcurrentHashMap<>();

    private final JedisPool jedisPool;

    public ConfigLoader() {
        log.info("Initializing ConfigLoader...");
        this.jedisPool = new JedisPool(new JedisPoolConfig(), REDIS_HOST, REDIS_PORT, REDIS_TIMEOUT, REDIS_PASSWORD);
        loadConfigsFromMySQL();
    }

    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    /**
     * 主动从 MySQL 全量加载并写入 Redis
     */
    public void loadConfigsFromMySQL() {
        Map<String, CounterMetricConfig> newConfigs = new ConcurrentHashMap<>();
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
             Jedis jedis = jedisPool.getResource()) {
            String sql = "SELECT * FROM counter_metric WHERE status = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CounterMetricConfig config = extractConfigFromResultSet(rs);
                newConfigs.put(config.getMetricCode(), config);
                jedis.setex(CONFIG_KEY_PREFIX + config.getMetricCode(), 24 * 3600, mapper.writeValueAsString(config));
            }
            configCache.clear();
            configCache.putAll(newConfigs);
            log.info("Loaded {} counter metric configs from MySQL", newConfigs.size());
        } catch (Exception e) {
            log.error("Failed to load configs from MySQL", e);
        }
    }

    /**
     * 按需读取多个配置，优先从 Redis，缺失时回源 MySQL
     */
    public List<CounterMetricConfig> getConfigsByMetricCodes(List<String> metricCodes) {
        List<CounterMetricConfig> result = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> keys = metricCodes.stream().map(code -> CONFIG_KEY_PREFIX + code).collect(Collectors.toList());
            List<String> redisValues = jedis.mget(keys.toArray(new String[0]));
            for (int i = 0; i < metricCodes.size(); i++) {
                String metricCode = metricCodes.get(i);
                String json = redisValues.get(i);
                CounterMetricConfig config = null;
                if (json != null) {
                    config = mapper.readValue(json, CounterMetricConfig.class);
                } else {
                    config = loadSingleConfigFromMySQL(metricCode);
                    if (config != null) {
                        jedis.setex(CONFIG_KEY_PREFIX + metricCode, 24 * 3600, mapper.writeValueAsString(config));
                    }
                }
                if (config != null && config.getStatus() == 1) {
                    result.add(config);
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch configs by codes", e);
        }
        return result;
    }

    /**
     * 定时刷新配置的方法，可由外部 scheduler 调用
     */
    public void refreshConfigs() {
        log.info("Refreshing configs...");
        loadConfigsFromMySQL();
    }

    private CounterMetricConfig loadSingleConfigFromMySQL(String metricCode) {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            String sql = "SELECT * FROM counter_metric WHERE metric_code = ? AND status = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, metricCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractConfigFromResultSet(rs);
            }
        } catch (Exception e) {
            log.error("Failed to load single config from MySQL for {}", metricCode, e);
        }
        return null;
    }

    private CounterMetricConfig extractConfigFromResultSet(ResultSet rs) throws SQLException {
        CounterMetricConfig config = new CounterMetricConfig();
        config.setIncidentCode(rs.getString("incident_code"));
        config.setAttributeKey(rs.getString("attribute_key"));
        config.setWindowSize(rs.getString("window_size"));
        config.setAggregationType(rs.getString("aggregation_type"));
        config.setMetricCode(rs.getString("metric_code"));
        config.setMetricName(rs.getString("metric_name"));
        config.setMetricType(rs.getInt("metric_type"));
        config.setStatus(rs.getInt("status"));
        return config;
    }
}
