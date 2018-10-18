package com.qinglan.sdk.server.release.application.basic.redis;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class RedisUtil {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private String defaultKey;

    private String defaultTime;

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    public void setDefaultTime(String defaultTime) {
        this.defaultTime = defaultTime;
    }

    private void lpush(String key, String val) {
        redisTemplate.opsForList().leftPush(key, val);
    }

    private String brpop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    private void set(String key, String obj, long time, TimeUnit n) {
        redisTemplate.opsForValue().set(key, obj, time, n);
    }

    private String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setKeyValue(String key, String value) {
        set(defaultKey + "_" + key, value, Long.valueOf(defaultTime), TimeUnit.SECONDS);
    }

    public void setKeyValue(String key, String value, long seconds) {
        set(defaultKey + "_" + key, value, seconds, TimeUnit.SECONDS);
    }

    public String getValue(String key) {
        return get(defaultKey + "_" + key);
    }

    public void setLpush(String vlaue) throws InterruptedException {
        lpush(defaultKey + "_Order", vlaue);
    }

    public String getBrpop() throws InterruptedException {
        return (String) brpop(defaultKey + "_Order");
    }

    public void setLpush(String key, String vlaue) throws InterruptedException {
        lpush(defaultKey + key, vlaue);
    }

    public String getBrpop(String key) throws InterruptedException {
        return (String) brpop(defaultKey + key);
    }

    public void increment(String key, int value) {
        redisTemplate.opsForValue().increment(defaultKey + "_" + key, value);
        redisTemplate.opsForValue().getOperations().expire(defaultKey + "_" + key, Long.parseLong(defaultTime), TimeUnit.SECONDS);
    }

}
