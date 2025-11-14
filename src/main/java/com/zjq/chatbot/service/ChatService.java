package com.zjq.chatbot.service;

import com.zjq.chatbot.entity.MessageEntity;
import com.zjq.chatbot.mapper.MessageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Resource
    private MessageMapper messageMapper;

    public int insert(MessageEntity message){
        return messageMapper.insert(message);
    }

    public List<MessageEntity> getMessagesBySid(Long sessionId) {
        return messageMapper.listBySessionId(sessionId);
    }
}
