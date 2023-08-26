package com.futurhero.community.service;

import com.futurhero.community.bean.Ticket;
import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    @Autowired
    private RedisTemplate redisTemplate;

    public Ticket findTicketByTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (Ticket) redisTemplate.opsForValue().get(ticketKey);
    }
}
