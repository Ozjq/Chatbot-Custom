package com.zjq.chatbot.mapper;

import com.zjq.chatbot.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    /**
     *
     * @param message
     * @return
     */
    int insert(MessageEntity message);

    MessageEntity getById(Long id);

    List<MessageEntity> listBySessionId(Long sessionId);
}
