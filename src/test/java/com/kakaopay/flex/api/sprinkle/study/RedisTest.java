package com.kakaopay.flex.api.sprinkle.study;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void t1() {
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        Map<String, Object> map = new HashMap<>();
        map.put("a", 123);
        vop.set("a", map);
        Object result = vop.get("a");
        System.out.println("===================" + result.toString());
    }
}
