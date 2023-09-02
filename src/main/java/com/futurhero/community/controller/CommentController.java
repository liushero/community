package com.futurhero.community.controller;

import com.futurhero.community.annotation.LoginRequired;
import com.futurhero.community.bean.Comment;
import com.futurhero.community.bean.Event;
import com.futurhero.community.bean.User;
import com.futurhero.community.event.EventProducer;
import com.futurhero.community.service.CommentService;
import com.futurhero.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer producer;

    @PostMapping("/add/{discussPostId}")
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") int discussPostId,
                             Comment comment, int entityType, int entityId, int targetId,int entityUserId) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setEntityType(entityType);
        comment.setEntityId(entityId);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment.setTargetId(targetId);

        commentService.addComment(comment);

        if (user.getId() != entityUserId) {
            Event event = new Event();
            event.setTopic("comment");
            event.setUserId(user.getId());
            event.setEntityType(entityType);
            event.setEntityId(entityId);
            event.setEntityUserId(entityUserId);
            event.setData("postId", discussPostId);
            producer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
