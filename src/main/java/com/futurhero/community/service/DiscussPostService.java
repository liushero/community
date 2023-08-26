package com.futurhero.community.service;

import com.futurhero.community.bean.DiscussPost;
import com.futurhero.community.dao.DiscussPostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;

    public List<DiscussPost> findDiscussPost(int offset, int limit) {
        return discussPostDao.selectDiscussPost(offset, limit);
    }

    public int findDiscussPostCount() {
        return discussPostDao.selectDiscussPostCount();
    }
}
