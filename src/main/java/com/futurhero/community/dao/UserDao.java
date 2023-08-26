package com.futurhero.community.dao;

import com.futurhero.community.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {
    User selectUserById(int id);

    int insertUser(User user);

    User selectUserByName(String username);

    User selectUserByEmail(String email);

    int updatePasswordById(@Param("id") int id, @Param("password") String password);

    int updateHeaderUrlById(@Param("id") int id, @Param("headerUrl") String headerUrl);

    int updateStatusById(@Param("id") int id, @Param("status") int status);
}
