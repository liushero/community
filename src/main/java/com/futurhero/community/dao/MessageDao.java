package com.futurhero.community.dao;

import com.futurhero.community.bean.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageDao {
    List<Message> selectLetterConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    int selectLetterConversationCount(int userId);

    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    int selectLetterCount(String conversationId);

    int selectLetterUnread(@Param("userId") int userId, @Param("conversationId") String conversationId);

    int updateLetterStatus(@Param("ids") List<Integer> ids);

    int insertLetter(Message message);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    List<Message> selectNoticeConversations(int userId);

    List<Message> selectNotices(@Param("userId") int userId, @Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    int selectNoticeCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    int selectNoticeUnread(@Param("userId") int userId, @Param("conversationId") String conversationId);

    int updateNoticeStatus(@Param("ids") List<Integer> ids);

    int insertNotice(Message message);
}
