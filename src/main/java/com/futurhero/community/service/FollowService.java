package com.futurhero.community.service;

import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param userId   当前用户的id
     * @param targetId 目标用户的id
     *                 userId 关注 / 取关 targetId
     */
    public void follow(int userId, int targetId) {
        String followerKey = RedisKeyUtil.getFollowerKey(targetId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                Double score = operations.opsForZSet().score(followeeKey, targetId);

                operations.multi();
                if (score != null) {
                    operations.opsForZSet().remove(followeeKey, targetId);
                    operations.opsForZSet().remove(followerKey, userId);
                } else {
                    operations.opsForZSet().add(followeeKey, targetId, System.currentTimeMillis());
                    operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                }
                return operations.exec();
            }
        });
    }

    public long getFollowerCount(int id) {
        String followerKey = RedisKeyUtil.getFollowerKey(id);
        return redisTemplate.opsForZSet().size(followerKey);
    }

    public Set<Integer> getFollowers(int id, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(id);
        return redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
    }

    public long getFolloweeCount(int id) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(id);
        return redisTemplate.opsForZSet().size(followeeKey);
    }

    public Set<Integer> getFollowees(int id, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(id);
        return redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
    }

    public int getFollowUserStatus(int userId, int targetId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId);
        Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
        return score == null ? 0 : 1;   // 0：没关注    1：关注了
    }
}
