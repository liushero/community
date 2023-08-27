package com.futurhero.community.controller;

import com.futurhero.community.annotation.LogoutRequired;
import com.futurhero.community.bean.Comment;
import com.futurhero.community.bean.DiscussPost;
import com.futurhero.community.bean.Page;
import com.futurhero.community.bean.User;
import com.futurhero.community.service.CommentService;
import com.futurhero.community.service.DiscussPostService;
import com.futurhero.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/detail/{discussPostId}")
    public String detail(Model model, @PathVariable("discussPostId") int id, Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("discussPost", post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 展示帖子的评论
        page.setLimit(5);
        page.setRows(commentService.findCommentCount(1, post.getId()));
        page.setPath("/discuss/detail/" + post.getId());

        List<Comment> comments = commentService.findComments(1, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentMaps = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("comment", comment);

            User u = userService.findUserById(comment.getUserId());
            map.put("user", u);

            List<Comment> replies = commentService.findComments(2, comment.getId(), 0, 0);
            List<Map<String, Object>> replyMaps = new ArrayList<>();
            for (Comment reply : replies) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("reply", reply);

                User u1 = userService.findUserById(reply.getUserId());
                map1.put("user", u1);

                User u2 = userService.findUserById(reply.getTargetId());
                map1.put("target", u2);

                replyMaps.add(map1);
            }
            map.put("map1", replyMaps);

            commentMaps.add(map);
        }
        model.addAttribute("count", post.getCommentCount());
        model.addAttribute("maps", commentMaps);
        return "/site/discuss-detail";
    }
}
