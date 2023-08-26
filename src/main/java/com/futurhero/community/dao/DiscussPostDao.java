package com.futurhero.community.dao;

import com.futurhero.community.bean.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostDao {
    List<DiscussPost> selectDiscussPost(@Param("offset") int offset, @Param("limit") int limit);

    int selectDiscussPostCount();
}
