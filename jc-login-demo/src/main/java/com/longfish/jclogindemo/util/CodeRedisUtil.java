package com.longfish.jclogindemo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.longfish.jclogindemo.constant.CommonConstant.CODE;

@Component
@SuppressWarnings("all")
public class CodeRedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    public void insert(String username, String code) {
        redisTemplate.opsForValue().set(CODE + "::" + username, code, 15, TimeUnit.MINUTES);
    }

    public String get(String username) {
        Object code = redisTemplate.opsForValue().get(CODE + "::" + username);
        if (code != null) return (String) code;
        return null;
    }
}
