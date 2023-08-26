package com.futurhero.community.util;

import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    public static String getStringUUID() {
        return UUID.randomUUID().toString().replace('-', 'z');
    }

    public static String getMD5(String password, String salt) {
        String epassword = password + salt;
        return DigestUtils.md5DigestAsHex(epassword.getBytes());
    }
}
