package com.zjq.chatbot.mapper;

import com.zjq.chatbot.entity.SessionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SessionMapper {
    int insert(SessionEntity session);

    SessionEntity getById(@Param("id") Long id);

    List<SessionEntity> listRecent(@Param("limit") int limit);

    List<SessionEntity> listRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    int bumpStats(@Param("id") Long id, @Param("ts") LocalDateTime ts);
}
