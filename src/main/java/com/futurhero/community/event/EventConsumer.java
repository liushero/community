package com.futurhero.community.event;

import com.alibaba.fastjson.JSONObject;
import com.futurhero.community.bean.DiscussPost;
import com.futurhero.community.bean.Event;
import com.futurhero.community.bean.Message;
import com.futurhero.community.service.DiscussPostService;
import com.futurhero.community.service.ElasticsearchService;
import com.futurhero.community.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private DiscussPostService discussPostService;

    @KafkaListener(topics = {"like", "comment", "follow"})
    public void handlerLKC(ConsumerRecord record) {
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event.getTopic().equals("like") || event.getTopic().equals("comment")) {
            Message message = new Message();
            message.setFromId(1);
            message.setToId(event.getEntityUserId());
            message.setConversationId(event.getTopic());
            Map<String, Object> map = new HashMap<>();
            map.put("entityType", event.getEntityType());
            map.put("entityId", event.getEntityId());
            map.put("postId", event.getData().get("postId"));
            map.put("userId", event.getUserId());
            message.setContent(JSONObject.toJSONString(map));
            message.setStatus(0);
            message.setCreateTime(new Date());

            messageService.addNotice(message);
        } else if (event.getTopic().equals("follow")) {
            Message message = new Message();
            message.setFromId(1);
            message.setToId(event.getEntityUserId());
            message.setConversationId(event.getTopic());
            Map<String, Object> map = new HashMap<>();
            map.put("entityType", event.getEntityType());
            map.put("entityId", event.getEntityId());
            map.put("userId", event.getUserId());
            message.setContent(JSONObject.toJSONString(map));
            message.setStatus(0);
            message.setCreateTime(new Date());

            messageService.addNotice(message);
        }
    }

    @KafkaListener(topics = {"post"})
    public void handlerP(ConsumerRecord record) {
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event.getTopic().equals("post")) {
            DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
            elasticsearchService.saveDiscussPost(discussPost);
        }
    }
}
