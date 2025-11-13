package com.zjq.chatbot.service;

import com.zjq.chatbot.entity.SessionEntity;
import com.zjq.chatbot.mapper.SessionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {

    @Resource
    private SessionMapper sessionMapper;

    public int insert(SessionEntity session){
        return sessionMapper.insert(session);
    }

    /** 近期会话列表（可用于首页“最近聊天”） */
    public List<SessionEntity> listRecent(int limit){
        return sessionMapper.listRecent(limit);
    }

    /**
     * 显式创建一个新会话（用于首次 /messages 无 sessionId 的情况）
     * 返回 DB 生成的 sessionId
     */
    public Long create() {
        SessionEntity toCreate = SessionEntity.builder()
                .anonId(java.util.UUID.randomUUID().toString()) // 如果表的 anon_id 可为空就留空；若不可为空，给个 UUID 用于内部追踪
                .title("新的会话")
                .channel("web")
                .messageCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        sessionMapper.insert(toCreate);
        return toCreate.getId();
    }
}
