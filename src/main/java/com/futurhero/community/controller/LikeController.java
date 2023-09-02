package com.futurhero.community.controller;

import com.futurhero.community.annotation.LoginRequired;
import com.futurhero.community.bean.Event;
import com.futurhero.community.bean.User;
import com.futurhero.community.event.EventProducer;
import com.futurhero.community.service.LikeService;
import com.futurhero.community.util.CommunityUtil;
import com.futurhero.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer producer;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int id, int entityUserId,int postId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(1, "未登录", null);
        }

        likeService.like(user.getId(), entityType, id, entityUserId);
        Map<String, Object> map = new HashMap<>();
        map.put("count", likeService.findEntityLikeCount(entityType, id));
        // 0表示点赞，1表示取消点赞
        map.put("status", likeService.findEntityLikeStatus(user.getId(), entityType, id) ? 0 : 1);

        if (user.getId() != entityUserId) {
            Event event = new Event();
            event.setTopic("like");
            event.setUserId(user.getId());
            event.setEntityType(entityType);
            event.setEntityId(id);
            event.setEntityUserId(entityUserId);
            event.setData("postId", postId);
            producer.fireEvent(event);
        }

        return CommunityUtil.getJsonString(0, null, map);
    }
}
