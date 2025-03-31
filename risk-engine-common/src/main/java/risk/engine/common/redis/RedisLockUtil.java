package risk.engine.common.redis;

/**
 * @Author: X
 * @Date: 2025/3/31 17:48
 * @Version: 1.0
 */
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_VALUE = "LOCKED";

    /**
     * 获取 Redis 单机锁（SETNX 实现）
     *
     * @param key        锁的 key
     * @param expireTime 过期时间（秒）
     * @return 是否加锁成功
     */
    public boolean tryLock(String key, long expireTime) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, LOCK_VALUE, expireTime, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放 Redis 单机锁（Lua 脚本保证原子性）
     *
     * @param key 锁的 key
     */
    public void unlock(String key) {
        String luaScript =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "   return redis.call('del', KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end";

        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Collections.singletonList(key), LOCK_VALUE);
    }
}
