package com.coder.community;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes() {
        String key = "test:students";

        redisTemplate.opsForZSet().add(key, "唐僧", 80);
        redisTemplate.opsForZSet().add(key, "悟空", 90);
        redisTemplate.opsForZSet().add(key, "八戒", 50);
        redisTemplate.opsForZSet().add(key, "沙僧", 60);
        redisTemplate.opsForZSet().add(key, "白龙马", 70);

        System.out.println(redisTemplate.opsForZSet().zCard(key));
        System.out.println(redisTemplate.opsForZSet().score(key, "八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(key, "八戒"));
        System.out.println(redisTemplate.opsForZSet().range(key, 0, 2));
        System.out.println(redisTemplate.opsForZSet().reverseRange(key, 0, 2));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:count");

        System.out.println(redisTemplate.hasKey("test:count"));

        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    // 多次访问同一个key
    @Test
    public void testBoundOperations() {
        String key = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(key);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    // 编程式事务
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String key = "test:tx";
                operations.multi(); // 启用事务

                operations.opsForSet().add(key, "zhangsan");
                operations.opsForSet().add(key, "lisi");
                operations.opsForSet().add(key, "wangwu");

                System.out.println(redisTemplate.opsForSet().members(key)); // 不会查到

                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
