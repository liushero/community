package com.futurhero.community.controller;

import com.futurhero.community.bean.User;
import com.futurhero.community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class HomeController {
    @Resource
    private UserService userService;

    @RequestMapping("/getuser")
    @ResponseBody
    public User getUser(int id) {
        return userService.findUserById(id);
    }
}
