package com.futurhero.community.service;

import com.futurhero.community.util.HostHolder;
import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param userId     当前用户的id
     * @param entityType 点赞的实体类型
     * @param id         点赞的实体id
     * @param entityUserId  实体的用户的id
     */
    public void like(int userId, int entityType, int id, int entityUserId) {
        // entityKey对应的value是set，保存点赞该实体的用户id
        String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, id);
        // userKey对应的value是该用户受到的点赞数量
        String likeUserKey = RedisKeyUtil.getLikeUserKey(entityUserId);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // redis事务内不能查询，因为没执行只是入队列
                Boolean isMember = operations.opsForSet().isMember(likeEntityKey, userId);

                operations.multi();
                if (isMember) {
                    // 取消点赞
                    operations.opsForSet().remove(likeEntityKey, userId);
                    operations.opsForValue().decrement(likeUserKey);
                } else {
                    // 点赞
                    operations.opsForSet().add(likeEntityKey, userId);
                    operations.opsForValue().increment(likeUserKey);
                }
                return operations.exec();
            }
        });
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int id) {
        String entityLikeKey = RedisKeyUtil.getLikeEntityKey(entityType, id);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态
    public boolean findEntityLikeStatus(int userId, int entityType, int id) {
        String entityLikeKey = RedisKeyUtil.getLikeEntityKey(entityType, id);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId);
    }

    // 查询某个用户获得的赞
    public long findUserLikeCount(int id) {
        String userLikeKey = RedisKeyUtil.getLikeUserKey(id);
        return redisTemplate.opsForValue().get(userLikeKey) == null ? 0 : (int) redisTemplate.opsForValue().get(userLikeKey);
    }

}
