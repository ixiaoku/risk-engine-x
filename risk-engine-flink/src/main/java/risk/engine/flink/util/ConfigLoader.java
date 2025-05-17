package risk.engine.flink.util;

/**
 * @Author: X
 * @Date: 2025/5/17 18:04
 * @Version: 1.0
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import risk.engine.flink.model.CounterMetricConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String MYSQL_URL = "jdbc:mysql://mysql:3306/risk_db?useSSL=false";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "dcr";
    private static final String REDIS_HOST = "redis";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = "dcr";
    private static final String CONFIG_CHANNEL = "feature_config_updates";
    private static final String CONFIG_KEY_PREFIX = "counter_metric_config:";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, CounterMetricConfig> configCache = new ConcurrentHashMap<>();

    public ConfigLoader() {
        loadConfigs();
        startRedisSubscriber();
    }

    public void loadConfigs() {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
             Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            jedis.auth(REDIS_PASSWORD);
            String sql = "SELECT * FROM counter_metric WHERE status = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            Map<String, CounterMetricConfig> newConfigs = new ConcurrentHashMap<>();
            while (rs.next()) {
                CounterMetricConfig config = new CounterMetricConfig();
                config.setIncidentCode(rs.getString("incident_code"));
                config.setAttributeKey(rs.getString("attribute_key"));
                config.setWindowSize(rs.getString("window_size"));
                config.setAggregationType(rs.getString("aggregation_type"));
                config.setMetricCode(rs.getString("metric_code"));
                config.setMetricName(rs.getString("metric_name"));
                config.setMetricType(rs.getInt("metric_type"));
                config.setStatus(rs.getInt("status"));
                newConfigs.put(config.getMetricCode(), config);
                jedis.setex(CONFIG_KEY_PREFIX + config.getMetricCode(), 24 * 3600,
                        mapper.writeValueAsString(config));
            }
            configCache.clear();
            configCache.putAll(newConfigs);
            LOG.info("Loaded {} feature configs", newConfigs.size());
        } catch (Exception e) {
            LOG.error("Failed to load configs", e);
        }
    }

    private void startRedisSubscriber() {
        new Thread(() -> {
            try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
                    jedis.auth(REDIS_PASSWORD);
                }
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        loadConfigs();
                        LOG.info("Received config update signal");
                    }
                }, CONFIG_CHANNEL);
            } catch (Exception e) {
                LOG.error("Redis subscriber failed", e);
            }
        }).start();
    }

    public List<CounterMetricConfig> getConfigsByMetricCodes(List<String> metricCodes) {
        List<CounterMetricConfig> configs = new ArrayList<>();
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
                jedis.auth(REDIS_PASSWORD);
            }
            for (String metricCode : metricCodes) {
                String json = jedis.get(CONFIG_KEY_PREFIX + metricCode);
                CounterMetricConfig config;
                if (json != null) {
                    config = mapper.readValue(json, CounterMetricConfig.class);
                } else {
                    config = loadConfigFromMySQL(metricCode);
                    if (config != null) {
                        jedis.setex(CONFIG_KEY_PREFIX + metricCode, 24 * 3600,
                                mapper.writeValueAsString(config));
                    }
                }
                if (config != null && config.getStatus() == 1) {
                    configs.add(config);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to get configs", e);
        }
        return configs;
    }

    private CounterMetricConfig loadConfigFromMySQL(String metricCode) {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            String sql = "SELECT * FROM counter_metric WHERE metric_code = ? AND is_active = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, metricCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
        } catch (Exception e) {
            LOG.error("Failed to load config from MySQL for {}", metricCode, e);
        }
        return null;
    }
}
