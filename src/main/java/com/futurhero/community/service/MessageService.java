package com.futurhero.community.service;

import com.futurhero.community.bean.Message;
import com.futurhero.community.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;

    public List<Message> findLetterConversations(int userId, int offset, int limit) {
        return messageDao.selectLetterConversations(userId, offset, limit);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageDao.selectLetters(conversationId, offset, limit);
    }

    public int findLetterConversationCount(int userId) {
        return messageDao.selectLetterConversationCount(userId);
    }

    public int findLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    public int findLetterUnread(int userId, String conversationId) {
        return messageDao.selectLetterUnread(userId, conversationId);
    }

    public int setLetterStatus(List<Integer> ids) {
        return messageDao.updateLetterStatus(ids);
    }

    public int addLetter(Message message) {
        return messageDao.insertLetter(message);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    public List<Message> findNoticeConversations(int userId) {
        return messageDao.selectNoticeConversations(userId);
    }

    public List<Message> findNotices(int userId, String conversationId, int offset, int limit) {
        return messageDao.selectNotices(userId, conversationId, offset, limit);
    }

    public int findNoticeCount(int userId, String conversationId) {
        return messageDao.selectNoticeCount(userId, conversationId);
    }

    public int findNoticeUnread(int userId,String conversationId) {
        return messageDao.selectNoticeUnread(userId, conversationId);
    }

    public int setNoticeStatus(List<Integer> ids) {
        return messageDao.updateNoticeStatus(ids);
    }

    public int addNotice(Message message) {
        return messageDao.insertNotice(message);
    }
}
