package com.futurhero.community.dao;

import com.futurhero.community.bean.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDao {

    // entityType指的是给哪种实体（帖子1、评论2）的评论，entityId指的是该实体的id
    List<Comment> selectComments(@Param("entityType") int entityType,
                                 @Param("entityId") int entityId,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    int selectCommentCount(@Param("entityType") int entityType,
                           @Param("entityId") int entityId);
}
