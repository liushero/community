package com.futurhero.community.service;

import com.futurhero.community.bean.Comment;
import com.futurhero.community.dao.CommentDao;
import com.futurhero.community.dao.DiscussPostDao;
import com.futurhero.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private DiscussPostDao discussPostDao;
    @Autowired
    private SensitiveFilter filter;

    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentDao.selectComments(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentDao.selectCommentCount(entityType, entityId);
    }

    /**
     * 插入评论时，需要同时更新帖子表中的comment_count字段，因此需要开启事务
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        comment.setContent(filter.filter(comment.getContent()));

        int rows = commentDao.insertComment(comment);
        if (comment.getEntityType() == 1) {
            discussPostDao.updateCountById(comment.getEntityId(), -1);
        }

        return rows;
    }
}
