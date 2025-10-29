package com.zjq.chatbot.mapper;

import com.zjq.chatbot.entity.SessionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SessionMapper {
    void insert(SessionEntity session);
}
