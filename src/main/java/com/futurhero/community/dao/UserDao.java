package com.futurhero.community.dao;

import com.futurhero.community.bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User selectUserById(int id);
}
