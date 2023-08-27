package com.futurhero.community.service;

import com.futurhero.community.bean.Comment;
import com.futurhero.community.dao.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentDao.selectComments(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentDao.selectCommentCount(entityType, entityId);
    }
}
