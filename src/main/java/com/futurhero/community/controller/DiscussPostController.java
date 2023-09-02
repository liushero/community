package com.futurhero.community.controller;

import com.futurhero.community.annotation.LoginRequired;
import com.futurhero.community.annotation.LogoutRequired;
import com.futurhero.community.bean.*;
import com.futurhero.community.event.EventProducer;
import com.futurhero.community.service.CommentService;
import com.futurhero.community.service.DiscussPostService;
import com.futurhero.community.service.LikeService;
import com.futurhero.community.service.UserService;
import com.futurhero.community.util.CommunityUtil;
import com.futurhero.community.util.HostHolder;
import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer producer;

    @GetMapping("/detail/{discussPostId}")
    public String detail(Model model, @PathVariable("discussPostId") int id, Page page) {
        User hostHolderUser = hostHolder.getUser();
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("discussPost", post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("likePostCount", likeService.findEntityLikeCount(0, id));
        model.addAttribute("likePostStatus", likeService.findEntityLikeStatus(hostHolderUser == null ? -1 : hostHolderUser.getId(), 0, id) ? 0 : 1);

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

            int commentCount = commentService.findCommentCount(2, comment.getId());
            map.put("count", commentCount);

            map.put("likeCommentCount", likeService.findEntityLikeCount(1, comment.getId()));
            map.put("likeCommentStatus", likeService.findEntityLikeStatus(hostHolderUser == null ? -1 : hostHolderUser.getId(), 1, comment.getId()) ? 0 : 1);

            List<Comment> replies = commentService.findComments(2, comment.getId(), 0, 0);
            List<Map<String, Object>> replyMaps = new ArrayList<>();
            for (Comment reply : replies) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("reply", reply);

                User u1 = userService.findUserById(reply.getUserId());
                map1.put("user", u1);

                User u2 = userService.findUserById(reply.getTargetId());
                map1.put("target", u2);

                map1.put("likeReplyCount", likeService.findEntityLikeCount(2, reply.getId()));
                map1.put("likeReplyStatus", likeService.findEntityLikeStatus(hostHolderUser == null ? -1 : hostHolderUser.getId(), 2, reply.getId()) ? 0 : 1);

                replyMaps.add(map1);
            }
            map.put("map1", replyMaps);

            commentMaps.add(map);
        }
        model.addAttribute("count", post.getCommentCount());
        model.addAttribute("maps", commentMaps);
        return "/site/discuss-detail";
    }

    /**
     * 通过Ajax实现异步添加帖子
     *
     * @param discussPost
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    @LoginRequired
    public String addDiscussPost(DiscussPost discussPost) {
        User user = hostHolder.getUser();
        discussPost.setStatus(0);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());

        discussPostService.addDiscussPost(discussPost);

        Event event = new Event();
        event.setTopic("post");
        event.setUserId(user.getId());
        event.setEntityType(0);
        event.setEntityId(discussPost.getId());
        event.setEntityUserId(user.getId());
        producer.fireEvent(event);

        return CommunityUtil.getJsonString(0, "发布成功", null);
    }
}
