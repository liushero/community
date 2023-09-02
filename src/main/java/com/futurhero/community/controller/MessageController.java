package com.futurhero.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.futurhero.community.annotation.LoginRequired;
import com.futurhero.community.bean.Message;
import com.futurhero.community.bean.Page;
import com.futurhero.community.bean.User;
import com.futurhero.community.service.MessageService;
import com.futurhero.community.service.UserService;
import com.futurhero.community.util.CommunityUtil;
import com.futurhero.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
    @LoginRequired
    public String letterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(10);
        page.setRows(messageService.findLetterConversationCount(user.getId()));
        page.setPath("/message/letter/list");

        List<Message> conversations = messageService.findLetterConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Message message : conversations) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", message);
            int targetId = message.getFromId() == user.getId() ? message.getToId() : message.getFromId();
            map.put("user", userService.findUserById(targetId));
            map.put("count", messageService.findLetterCount(message.getConversationId()));
            map.put("unread", messageService.findLetterUnread(user.getId(), message.getConversationId()));

            maps.add(map);
        }
        model.addAttribute("maps", maps);
        model.addAttribute("letterUnread", messageService.findLetterUnread(user.getId(), null));
        model.addAttribute("noticeUnread", messageService.findNoticeUnread(user.getId(), null));
        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    @LoginRequired
    public String letterDetail(Model model, @PathVariable("conversationId") String conversationId, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(10);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/message/letter/detail/" + conversationId);

        List<Message> messages = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", message);
            map.put("from", userService.findUserById(message.getFromId()));

            maps.add(map);
        }
        model.addAttribute("maps", maps);
        String[] split = conversationId.split("_");
        int targetId = Integer.parseInt(split[0]) == user.getId() ? Integer.parseInt(split[1]) : Integer.parseInt(split[0]);
        model.addAttribute("target", userService.findUserById(targetId));

        List<Integer> ids = findIds(messages, user.getId());
        if (ids.size() != 0) {
            messageService.setLetterStatus(ids);
        }

        return "/site/letter-detail";
    }

    private List<Integer> findIds(List<Message> messages, int userId) {
        List<Integer> ids = new ArrayList<>();
        for (Message message : messages) {
            if (message.getStatus() == 0 && message.getToId() == userId) {
                ids.add(message.getId());
            }
        }
        return ids;
    }

    @PostMapping("/letter/send")
    @LoginRequired
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJsonString(1, "用户不存在", null);
        }
        User user = hostHolder.getUser();
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(target.getId());
        String conversationId = Math.min(user.getId(), target.getId()) + "_" + Math.max(user.getId(), target.getId());
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());

        messageService.addLetter(message);
        return CommunityUtil.getJsonString(0, "发送成功", null);
    }

    @GetMapping("/notice/list")
    @LoginRequired
    public String noticeList(Model model) {
        User user = hostHolder.getUser();

        List<Message> noticeConversations = messageService.findNoticeConversations(user.getId());
        for (Message notice : noticeConversations) {
            if (notice.getConversationId().equals("like")) {
                model.addAttribute("like", notice);
                model.addAttribute("likeUnread", messageService.findNoticeUnread(user.getId(), notice.getConversationId()));
                model.addAttribute("likeCount", messageService.findNoticeCount(user.getId(), notice.getConversationId()));
                HashMap map = JSONObject.parseObject(notice.getContent(), HashMap.class);
                model.addAttribute("likeUser", userService.findUserById((Integer) map.get("userId")));
                model.addAttribute("likeType", map.get("entityType"));
            } else if (notice.getConversationId().equals("follow")) {
                model.addAttribute("follow", notice);
                model.addAttribute("followUnread", messageService.findNoticeUnread(user.getId(), notice.getConversationId()));
                model.addAttribute("followCount", messageService.findNoticeCount(user.getId(), notice.getConversationId()));
                HashMap map = JSONObject.parseObject(notice.getContent(), HashMap.class);
                model.addAttribute("followUser", userService.findUserById((Integer) map.get("userId")));
            } else if (notice.getConversationId().equals("comment")) {
                model.addAttribute("comment", notice);
                model.addAttribute("commentUnread", messageService.findNoticeUnread(user.getId(), notice.getConversationId()));
                model.addAttribute("commentCount", messageService.findNoticeCount(user.getId(), notice.getConversationId()));
                HashMap map = JSONObject.parseObject(notice.getContent(), HashMap.class);
                model.addAttribute("commentUser", userService.findUserById((Integer) map.get("userId")));
                model.addAttribute("commentType", map.get("entityType"));
            }
        }
        model.addAttribute("noticeUnread", messageService.findNoticeUnread(user.getId(), null));
        model.addAttribute("letterUnread", messageService.findLetterUnread(user.getId(), null));

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{conversationId}")
    @LoginRequired
    public String noticeDetail(Model model, @PathVariable("conversationId") String conversationId, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(10);
        page.setRows(messageService.findNoticeCount(user.getId(), conversationId));
        page.setPath("/message/notice/detail/" + conversationId);

        List<Message> notices = messageService.findNotices(user.getId(), conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> maps = new ArrayList<>();
        if (conversationId.equals("like") || conversationId.equals("comment")) {
            for (Message notice : notices) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                HashMap content = JSONObject.parseObject(notice.getContent(), HashMap.class);
                map.put("user", userService.findUserById((Integer) content.get("userId")));
                map.put("entityType", content.get("entityType"));
                map.put("postId", content.get("postId"));
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                maps.add(map);
            }
        } else {
            for (Message notice : notices) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                HashMap content = JSONObject.parseObject(notice.getContent(), HashMap.class);
                map.put("user", userService.findUserById((Integer) content.get("userId")));
                map.put("entityType", content.get("entityType"));
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                maps.add(map);
            }
        }
        model.addAttribute("maps", maps);
        model.addAttribute("conversationId", conversationId);

        List<Integer> ids = findIds(notices, user.getId());
        if (ids.size() != 0) {
            messageService.setNoticeStatus(findIds(notices, user.getId()));
        }

        return "/site/notice-detail";
    }
}
