package com.futurhero.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_KAPTCHA = "kaptcha";

    public static String getUerKey(int id) {
        return PREFIX_USER + SPLIT + id;
    }

    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getKaptchaKey(String random) {
        return PREFIX_KAPTCHA + SPLIT + random;
    }
}
