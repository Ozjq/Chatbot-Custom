package com.zjq.chatbot.mapper;

import com.zjq.chatbot.entity.SessionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SessionMapper {
    int insert(SessionEntity session);

    SessionEntity getById(@Param("id") Long id);
}
