package risk.engine.flink.sink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import risk.engine.flink.model.FeatureResult;

/**
 * @Author: X
 * @Date: 2025/3/14 16:55
 * @Version: 1.0
 */
@Slf4j
public class RedisSink extends RichSinkFunction<FeatureResult> {

    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;

    private transient JedisPool jedisPool;

    public RedisSink(String redisHost, int redisPort, String redisPassword) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);         // 最大连接数
        poolConfig.setMaxIdle(10);           // 最大空闲连接数
        poolConfig.setMinIdle(2);            // 最小空闲连接数
        poolConfig.setTestOnBorrow(true);    // 检查连接可用性
        poolConfig.setTestWhileIdle(true);

        if (redisPassword != null && !redisPassword.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
        } else {
            jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
        }
        log.info("RedisSink 初始化完成，连接到 {}:{}", redisHost, redisPort);
    }

    @Override
    public void invoke(FeatureResult result, Context context) {
        log.info("RedisSink 处理 FeatureResult: {}", result);
        try (Jedis jedis = jedisPool.getResource()) {
            String key = result.getMetricCode() + ":" + result.getUid();
            String value = String.valueOf(result.getValue());
            jedis.set(key, value);
            jedis.expire(key, result.getWindowSizeSeconds()); // 设置过期时间
            log.info("RedisSink 存入 Redis: key={}, value={}", key, value);
        } catch (Exception e) {
            // 建议记录日志
            log.error("RedisSink 写入失败: {}", e.getMessage(), e);
            // 可以加重试、报警、降级逻辑
        }
    }

    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}