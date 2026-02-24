package com.zjq.chatbot.service;

import com.zjq.chatbot.config.UserContext;
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

    public SessionEntity getSessionById(Long sessionId) {
        return sessionMapper.getById(sessionId);
    }

    public int insert(SessionEntity session){
        return sessionMapper.insert(session);
    }

    /** 近期会话列表（可用于首页“最近聊天”） */
    public List<SessionEntity> listRecent(int limit) {
        Long userId = UserContext.getUserId();
        // 如果用户已登录，只查询该用户的会话
        if (userId != null) {
            return sessionMapper.listRecentByUserId(userId, limit);
        }
        // 如果未登录，返回空列表或者匿名会话（取决于业务需求）
        return sessionMapper.listRecent(limit);
    }

    /**
     * 更新会话最后一次对话时间
     * @param sessionId
     */
    public void messageAppend(Long sessionId) {
        sessionMapper.bumpStats(sessionId, LocalDateTime.now());
    }


    /**
     * 显式创建一个新会话（用于首次 /messages 无 sessionId 的情况）
     * 返回 DB 生成的 sessionId
     */
    public Long create() {
        Long userId = UserContext.getUserId();
        
        SessionEntity toCreate = SessionEntity.builder()
                .userId(userId) // 关联当前登录用户
                .anonId(java.util.UUID.randomUUID().toString())
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
