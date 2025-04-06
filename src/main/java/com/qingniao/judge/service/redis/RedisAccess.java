package com.qingniao.judge.service.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class RedisAccess{
    private RedisTemplate<String, Object> redisTemplate;

    public void setExpireTime(String key, long time, TimeUnit timeUnit) {
        if(time <= 0)
            throw new RuntimeException("wrong time");
        redisTemplate.expire(key, time, timeUnit);
    }

    public long getExpireTime(String key, TimeUnit timeUnit) {
        Long time = redisTemplate.getExpire(key, timeUnit);
        return time;
    }

    public void setPersist(String key) {
        Boolean result = redisTemplate.persist(key);
        if(!result)
            throw new RuntimeException("setPersist error: "+ key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public boolean hasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public void delete(String key) {
        Boolean result = redisTemplate.delete(key);
        if(!result)
            throw new RuntimeException("delete error: "+ key);
    }

    public void delete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        if (time > 0)
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        else
            this.set(key, value);
    }

    public void set(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Object get(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }
}
