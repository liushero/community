package com.futurhero.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    // 实体的赞
    private static final String PREFIX_LIKE_ENTITY = "like:entity";
    // 用户的赞
    private static final String PREFIX_LIKE_USER = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";   // 当前用户
    private static final String PREFIX_FOLLOWER = "follower";   // 目标用户

    public static String getUerKey(int id) {
        return PREFIX_USER + SPLIT + id;
    }

    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getKaptchaKey(String random) {
        return PREFIX_KAPTCHA + SPLIT + random;
    }

    /**
     * @param entityType
     * @param id 与entityId不一样
     * @return
     */
    public static String getLikeEntityKey(int entityType, int id) {
        return PREFIX_LIKE_ENTITY + SPLIT + entityType + SPLIT + id;
    }

    public static String getLikeUserKey(int id) {
        return PREFIX_LIKE_USER + SPLIT + id;
    }

    public static String getFolloweeKey(int id) {
        return PREFIX_FOLLOWEE + SPLIT + id;
    }

    public static String getFollowerKey(int id) {
        return PREFIX_FOLLOWER + SPLIT + id;
    }
}
