package risk.engine.common.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: X
 * @Date: 2025/3/31 17:28
 * @Version: 1.0
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    @Resource
//    private RedissonClient redissonClient;

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set value for key: " + key, e);
        }
    }

    public void set(String key, Object value, long expireTime, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, unit);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set value for key: " + key, e);
        }
    }

    /**
     * 自增计数器
     *
     * @param key         Redis key
     * @param delta       增加的值（默认传 1）
     * @param expireTime  过期时间（可选，为 null 不设置）
     */
    public void increment(String key, long delta, Duration expireTime) {
        stringRedisTemplate.opsForValue().increment(key, delta);
        if (expireTime != null && stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.expire(key, expireTime);
        }
    }

    /**
     * 获取当前计数值
     *
     * @param key Redis key
     * @return 当前值，默认为 0
     */
    public long getCount(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key))
                .map(Long::valueOf)
                .orElse(0L);
    }

    public void set(String key, Object value, long expireTime) {
        set(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 锁
     * setNX命令 判断key是否存在
     * @param key 健值对
     * @param lockValue LOCK_VALUE 保证同一个客户端才能正确释放自己的锁
     * @param expireTime 过期时间
     * @return 结果
     */
    public Boolean setNX(String key, String lockValue, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(key, lockValue, expireTime, TimeUnit.SECONDS);
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get value for key: " + key, e);
        }
    }

    public void del(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete value for key: " + key, e);
        }
    }

    // --- Hash Operations ---
    public void hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set hash value for key: " + key, e);
        }
    }

    public Object hget(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get hash value for key: " + key, e);
        }
    }

    // --- List Operations ---
    public void lpush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to push to list for key: " + key, e);
        }
    }

    public Object rpop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to pop from list for key: " + key, e);
        }
    }

    // --- Set Operations ---
    public void sadd(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add to set for key: " + key, e);
        }
    }

    public boolean sismember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            throw new RuntimeException("Failed to check membership for key: " + key, e);
        }
    }

    // --- Sorted Set Operations ---
    public void zadd(String key, double score, Object value) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add to sorted set for key: " + key, e);
        }
    }

    public Set<Object> zrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            throw new RuntimeException("Failed to range sorted set for key: " + key, e);
        }
    }

    // --- Redisson Distributed Lock ---
//    public RLock getLock(String lockKey) {
//        return redissonClient.getLock(lockKey);
//    }

//    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
//        try {
//            RLock lock = redissonClient.getLock(lockKey);
//            return lock.tryLock(waitTime, leaseTime, unit);
//        } catch (Exception e) {
//            return false;
//        }
//    }

//    public void unlock(String lockKey) {
//        RLock lock = redissonClient.getLock(lockKey);
//        if (lock.isHeldByCurrentThread()) {
//            lock.unlock();
//        }
//    }

    // --- Redisson Atomic Operation ---
//    public <T> RBucket<T> getBucket(String key) {
//        return redissonClient.getBucket(key);
//    }
}
