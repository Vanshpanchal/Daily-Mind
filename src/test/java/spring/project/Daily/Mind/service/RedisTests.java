package spring.project.Daily.Mind.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void checkRedis(){
//        redisTemplate.opsForValue().set("sala","v@gmail.com");
//        Object email = redisTemplate.opsForValue().get("email");
        Object salary = redisTemplate.opsForValue().get("salary");

        int a = 1;
    }
}
