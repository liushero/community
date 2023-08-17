package com.futurhero.community.service.impl;

import com.futurhero.community.bean.User;
import com.futurhero.community.dao.UserDao;
import com.futurhero.community.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public User findUserById(int id) {
        return userDao.selectUserById(id);
    }
}
