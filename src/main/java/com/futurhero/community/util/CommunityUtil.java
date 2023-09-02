package com.futurhero.community.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    public static String getStringUUID() {
        return UUID.randomUUID().toString().replace('-', 'z');
    }

    public static String getMD5(String password, String salt) {
        String epassword = password + salt;
        return DigestUtils.md5DigestAsHex(epassword.getBytes());
    }

    public static String getJsonString(int code, String msg, Map<String, Object> data) {
        JSONObject object = new JSONObject();
        object.put("code", code);
        object.put("msg", msg);
        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                object.put(entry.getKey(), entry.getValue());
            }
        }
        return object.toJSONString();
    }
}
