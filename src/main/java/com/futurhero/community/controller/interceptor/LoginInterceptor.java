package com.futurhero.community.controller.interceptor;

import com.futurhero.community.bean.Ticket;
import com.futurhero.community.bean.User;
import com.futurhero.community.service.TicketService;
import com.futurhero.community.service.UserService;
import com.futurhero.community.util.HostHolder;
import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = getTicket(request.getCookies());
        if (ticket != null) {
            Ticket t = ticketService.findTicketByTicket(ticket);
            if (t != null && t.getStatus() != 1 && t.getExpired().after(new Date())) {
                User user = userService.findUserById(t.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }

    public String getTicket(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("ticket")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
