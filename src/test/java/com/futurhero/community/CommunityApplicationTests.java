package com.futurhero.community;

import com.alibaba.fastjson.JSONObject;
import com.futurhero.community.bean.User;
import com.futurhero.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
class CommunityApplicationTests {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() {

    }

    @Test
    void test1() {
        System.out.println(CommunityUtil.getMD5("lhr707198", "16a41"));
    }

}
