package com.zjq.chatbot.mapper;

import com.zjq.chatbot.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {
    void insert(MessageEntity message);
}
