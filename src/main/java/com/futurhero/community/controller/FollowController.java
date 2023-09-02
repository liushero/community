package com.futurhero.community.controller;

import com.futurhero.community.annotation.LoginRequired;
import com.futurhero.community.bean.Event;
import com.futurhero.community.bean.Page;
import com.futurhero.community.bean.User;
import com.futurhero.community.event.EventProducer;
import com.futurhero.community.service.FollowService;
import com.futurhero.community.service.UserService;
import com.futurhero.community.util.CommunityUtil;
import com.futurhero.community.util.HostHolder;
import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private EventProducer producer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int targetId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(1, "未登录", null);
        }
        followService.follow(user.getId(), targetId);
        Map<String, Object> map = new HashMap<>();
        map.put("count", followService.getFollowerCount(targetId));
        map.put("status", followService.getFollowUserStatus(user.getId(), targetId));

        Event event = new Event();
        event.setTopic("follow");
        event.setUserId(user.getId());
        event.setEntityType(3);
        event.setEntityId(targetId);
        event.setEntityUserId(targetId);
        producer.fireEvent(event);

        return CommunityUtil.getJsonString(0, null, map);
    }

    @GetMapping("/followee/{userId}")
    @LoginRequired
    public String getFollowees(Model model, @PathVariable("userId") int userId, Page page) {
        page.setLimit(10);
        page.setRows((int) followService.getFolloweeCount(userId));
        page.setPath("/followee/" + userId);

        Set<Integer> followees = followService.getFollowees(userId, page.getOffset(), page.getLimit());
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Integer i : followees) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(i));
            map.put("status", followService.getFollowUserStatus(hostHolder.getUser().getId(), i));
            map.put("date", new Date((long) redisTemplate.opsForZSet().score(followeeKey, i).doubleValue()));
            maps.add(map);
        }
        model.addAttribute("maps", maps);
        model.addAttribute("user", userService.findUserById(userId));
        return "/site/followee";
    }

    @GetMapping("/follower/{userId}")
    @LoginRequired
    public String getFollowers(Model model, @PathVariable("userId") int userId, Page page) {
        page.setLimit(10);
        page.setRows((int) followService.getFollowerCount(userId));
        page.setPath("/follower/" + userId);

        Set<Integer> followers = followService.getFollowers(userId, page.getOffset(), page.getLimit());
        String followerKey = RedisKeyUtil.getFollowerKey(userId);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Integer i : followers) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(i));
            map.put("status", followService.getFollowUserStatus(hostHolder.getUser().getId(), i));
            map.put("date", new Date((long) redisTemplate.opsForZSet().score(followerKey, i).doubleValue()));
            maps.add(map);
        }
        model.addAttribute("maps", maps);
        model.addAttribute("user", userService.findUserById(userId));
        return "/site/follower";
    }
}
