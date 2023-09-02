package com.futurhero.community.controller;

import com.futurhero.community.bean.DiscussPost;
import com.futurhero.community.bean.Page;
import com.futurhero.community.service.CommentService;
import com.futurhero.community.service.ElasticsearchService;
import com.futurhero.community.service.LikeService;
import com.futurhero.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/search")
    public String search(Model model, Page page, String keyword) {
        page.setLimit(10);
        org.springframework.data.domain.Page<DiscussPost> discussPosts = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        page.setRows(discussPosts == null ? 0 : discussPosts.getTotalPages());
        page.setPath("/search?keyword=" + keyword);

        List<Map<String, Object>> maps = new ArrayList<>();
        if (discussPosts != null) {
            for (DiscussPost discussPost : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                map.put("discussPost", discussPost);
                map.put("user", userService.findUserById(discussPost.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(0, discussPost.getId()));
                map.put("commentCount", commentService.findCommentCount(1, discussPost.getId()));

                maps.add(map);
            }
        }
        model.addAttribute("maps", maps);

        return "/site/search";
    }
}
