package com.futurhero.community.service;

import com.futurhero.community.bean.Ticket;
import com.futurhero.community.bean.User;
import com.futurhero.community.dao.UserDao;
import com.futurhero.community.util.CommunityUtil;
import com.futurhero.community.util.HostHolder;
import com.futurhero.community.util.MailClient;
import com.futurhero.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contexPath;

    /**
     * 先查redis，没有的话，再查数据库，并更新redis
     * user对象需要设置过期时间
     *
     * @param id
     * @return
     */
    public User findUserById(int id) {
        String uerKey = RedisKeyUtil.getUerKey(id);
        User user = (User) redisTemplate.opsForValue().get(uerKey);
        if (user == null) {
            user = userDao.selectUserById(id);
            redisTemplate.opsForValue().set(uerKey, user, 1, TimeUnit.HOURS);
        }
        return user;
    }

    /**
     * 数据库中的user一旦更新，就需要把缓存中对应的user删除，避免脏读
     *
     * @param id
     */
    public void deleteRedisUser(int id) {
        String uerKey = RedisKeyUtil.getUerKey(id);
        redisTemplate.delete(uerKey);
    }

    public Map<String, Object> register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 记录业务处理中的结果
        Map<String, Object> map = new HashMap<>();
        if (userDao.selectUserByName(user.getUsername()) != null) {
            map.put("nameMsg", "账号已存在");
            return map;
        }
        if (user.getPassword().length() < 8) {
            map.put("passwordMsg", "密码不能小于8位");
            return map;
        }
        if (userDao.selectUserByEmail(user.getEmail()) != null) {
            map.put("emailMsg", "邮箱已被注册");
            return map;
        }
        String salt = CommunityUtil.getStringUUID().substring(0, 5);
        user.setPassword(CommunityUtil.getMD5(user.getPassword(), salt));
        user.setSalt(salt);
        user.setStatus(0);
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        // id自增且赋值给对应属性
        userDao.insertUser(user);

        // context相当于model，可以往里添加数据
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 激活请求的格式为http://localhost:8080/community/activation/{userId}
        context.setVariable("url", domain + contexPath + "/activation/" + user.getId());
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        map.put("msg", "注册成功，请前往邮箱激活");
        return map;
    }

    public User findUserByName(String username) {
        return userDao.selectUserByName(username);
    }

    public int setPasswordById(int id, String password) {
        User user = userDao.selectUserById(id);
        password = CommunityUtil.getMD5(password, user.getSalt());
        int rows = userDao.updatePasswordById(id, password);
        deleteRedisUser(id);
        return rows;
    }

    public int setHeaderUrlById(int id, String headerUrl) {
        int rows = userDao.updateHeaderUrlById(id, headerUrl);
        deleteRedisUser(id);
        return rows;
    }

    public int setStatusById(int id, int status) {
        int rows = userDao.updateStatusById(id, status);
        deleteRedisUser(id);
        return rows;
    }

    public Map<String, Object> login(User user, boolean isRemember) {
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 记录业务处理中的结果
        Map<String, Object> map = new HashMap<>();
        // 不查询数据库的判断应该放在前面进行
        if (user.getUsername() == null) {
            map.put("nameMsg", "用户名为空");
            return map;
        }
        if (user.getPassword() == null) {
            map.put("passwordMsg", "密码为空");
            return map;
        }
        // 查询数据库依次判断账号和密码是否正确
        User u = userDao.selectUserByName(user.getUsername());
        if (u == null) {
            map.put("nameMsg", "用户名不存在");
            return map;
        }
        String password = CommunityUtil.getMD5(user.getPassword(), u.getSalt());
        if (!password.equals(u.getPassword())) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        // 生成登陆凭证，并保存到redis里
        Ticket ticket = new Ticket();
        ticket.setUserId(u.getId());
        ticket.setStatus(0);
        ticket.setTicket(CommunityUtil.getStringUUID());
        ticket.setExpired(isRemember ? new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14)
                : new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3));
        // 登陆凭证不删除，保留，便于以后统计相关信息，比如：最早登录的日期、最晚登陆时间是几点
        String ticketKey = RedisKeyUtil.getTicketKey(ticket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, ticket);
        // 便于客户端保存cookie
        map.put("ticket", ticket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        Ticket t = (Ticket) redisTemplate.opsForValue().get(ticketKey);
        if (t != null) {
            t.setStatus(1);
            redisTemplate.opsForValue().set(ticketKey, t);
        }
    }
}
