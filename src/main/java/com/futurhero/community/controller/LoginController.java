package com.futurhero.community.controller;

import com.futurhero.community.annotation.LoginRequired;
import com.futurhero.community.annotation.LogoutRequired;
import com.futurhero.community.bean.User;
import com.futurhero.community.service.UserService;
import com.futurhero.community.util.CommunityUtil;
import com.futurhero.community.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptcha;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/register")
    @LogoutRequired
    public String getRegister() {
        return "/site/register";
    }

    @GetMapping("/login")
    @LogoutRequired
    public String getLogin() {
        return "/site/login";
    }

    @PostMapping("/register")
    @LogoutRequired
    public String userRegister(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map.get("msg") != null) {
            model.addAttribute("res", map.get("msg"));
            return "/site/operate-result";
        }
        model.addAttribute("nameMsg", map.get("nameMsg"));
        model.addAttribute("passwordMsg", map.get("passwordMsg"));
        model.addAttribute("emailMsg", map.get("emailMsg"));
        return "/site/register";
    }

    @GetMapping("/activation/{userId}")
    public String activate(Model model, @PathVariable("userId") int id) {
        userService.setStatusById(id, 1);
        model.addAttribute("res", "账号已激活，请登录");
        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    @LogoutRequired
    public void kaptcha(HttpServletResponse response) {
        String text = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(text);

        String random = CommunityUtil.getStringUUID();
        Cookie cookie = new Cookie("kaptcha", random);
        cookie.setMaxAge(60);
        response.addCookie(cookie);
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(random);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error(e.getMessage() + "响应验证码失败");
        }
    }

    @PostMapping("/login")
    @LogoutRequired
    public String login(Model model, User user, boolean isRemember,
                        String kaptcha, @CookieValue("kaptcha") String random,
                        HttpServletResponse response) {
        model.addAttribute("isRemember", isRemember);
        String text = null;
        if (random != null) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(random);
            text = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }
        if (random == null || text == null || !text.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("kaptcha", "验证码错误");
            return "/site/login";
        }
        Map<String, Object> map = userService.login(user, isRemember);
        if (map.get("nameMsg") != null) {
            model.addAttribute("nameMsg", map.get("nameMsg"));
            return "/site/login";
        }
        if (map.get("passwordMsg") != null) {
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
        // 登陆成功，需要给客户端发送cookie
        if (map.get("ticket") != null) {
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            if (isRemember) {
                cookie.setMaxAge(60 * 60 * 24 * 14);
            } else {
                cookie.setMaxAge(60 * 60 * 24 * 3);
            }
            response.addCookie(cookie);
        }
        return "redirect:/index";
    }

    @GetMapping("/logout")
    @LoginRequired
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/index";
    }
}
