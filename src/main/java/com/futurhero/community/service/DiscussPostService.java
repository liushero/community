package com.futurhero.community.service;

import com.futurhero.community.bean.DiscussPost;
import com.futurhero.community.dao.DiscussPostDao;
import com.futurhero.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;
    @Autowired
    private SensitiveFilter filter;

    public List<DiscussPost> findDiscussPost(int offset, int limit) {
        return discussPostDao.selectDiscussPost(offset, limit);
    }

    public int findDiscussPostCount() {
        return discussPostDao.selectDiscussPostCount();
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostDao.selectDiscussPostById(id);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        discussPost.setTitle(filter.filter(discussPost.getTitle()));
        discussPost.setContent(filter.filter(discussPost.getContent()));

        return discussPostDao.insertDiscussPost(discussPost);
    }
}
