package com.futurhero.community.controller;

import com.futurhero.community.bean.DiscussPost;
import com.futurhero.community.bean.Page;
import com.futurhero.community.bean.User;
import com.futurhero.community.service.*;
import com.futurhero.community.util.HostHolder;
import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/index")
    public String getIndex(Model model, Page page) {
        page.setLimit(10);
        page.setRows(discussPostService.findDiscussPostCount());
        page.setPath("/index");

        List<DiscussPost> discussPosts = discussPostService.findDiscussPost(page.getOffset(), page.getLimit());
        List<Map<String, Object>> maps = new ArrayList<>();
        for (DiscussPost discussPost : discussPosts) {
            Map<String, Object> map = new HashMap<>();
            map.put("discussPost", discussPost);

            int userId = discussPost.getUserId();
            User user = userService.findUserById(userId);
            map.put("user", user);

            map.put("likeCount", likeService.findEntityLikeCount(0, discussPost.getId()));

            map.put("commentCount", commentService.findCommentCount(1, discussPost.getId()));

            maps.add(map);
        }
        model.addAttribute("maps", maps);

        return "/index";    // 这两个/index区别在于，一个是请求路径，一个是模板路径
    }
}
