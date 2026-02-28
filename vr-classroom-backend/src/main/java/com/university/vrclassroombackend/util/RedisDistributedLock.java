package com.university.vrclassroombackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisDistributedLock {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long DEFAULT_EXPIRE_TIME = 30;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    public boolean tryLock(String key) {
        return tryLock(key, DEFAULT_EXPIRE_TIME, DEFAULT_TIME_UNIT);
    }

    public boolean tryLock(String key, long expireTime, TimeUnit timeUnit) {
        String lockKey = "lock:" + key;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", expireTime, timeUnit);
        return Boolean.TRUE.equals(locked);
    }

    public void unlock(String key) {
        String lockKey = "lock:" + key;
        redisTemplate.delete(lockKey);
    }

    public boolean lockAndExecute(String key, Runnable task) {
        return lockAndExecute(key, DEFAULT_EXPIRE_TIME, DEFAULT_TIME_UNIT, task);
    }

    public boolean lockAndExecute(String key, long expireTime, TimeUnit timeUnit, Runnable task) {
        if (tryLock(key, expireTime, timeUnit)) {
            try {
                task.run();
                return true;
            } finally {
                unlock(key);
            }
        }
        return false;
    }
}