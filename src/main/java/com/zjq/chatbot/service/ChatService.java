package com.zjq.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjq.chatbot.entity.ChatMessageDTO;
import com.zjq.chatbot.entity.MessageEntity;
import com.zjq.chatbot.mapper.MessageMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ChatService {

    @Resource
    private MessageMapper messageMapper;
    private static final int MAX_RECENT_MESSAGES = 20;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    private ObjectMapper objectMapper; // 可以直接用 Spring Boot 自动注入的那个
    private static final String REDIS_PREFIX = "chat:session:";

    public int insert(MessageEntity message) {
        int id = messageMapper.insert(message);
        cacheMessageToRedis(message);
        return id;
    }

//    public List<MessageEntity> getMessagesBySid(Long sessionId) {
//        return messageMapper.listBySessionId(sessionId);
//    }

    private void cacheMessageToRedis(MessageEntity message) {
        Long sessionId = message.getSessionId();
        if(sessionId == null) {
            return;
        }
        String key = REDIS_PREFIX + sessionId;
        ChatMessageDTO dto = ChatMessageDTO.fromEntity(message);
        String json;
        try {
            json = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            return; //序列化失败不影响主流程
        }

        stringRedisTemplate.opsForList().rightPush(key,json);
        stringRedisTemplate.opsForList().trim(key,0,MAX_RECENT_MESSAGES-1);
        stringRedisTemplate.expire(key, Duration.ofDays(7));

    }

    /**
     * 前端拉取会话历史消息：优先从 Redis 拿，不存在再查 DB，并顺便回填 Redis。
     * 这里直接返回 DTO 更适合前端使用。
     */
    public List<ChatMessageDTO> getMessagesBySid(Long sessionId) {
        String key = REDIS_PREFIX + sessionId;

        // 1. 先从 Redis 读
        List<String> jsonList = stringRedisTemplate.opsForList().range(key, 0, MAX_RECENT_MESSAGES - 1);
        if (jsonList != null && !jsonList.isEmpty()) {
            return jsonList.stream()
                    .map(this::toDtoQuietly)
                    .filter(dto -> dto != null)
                    .toList();
        }

        // 2. Redis 没数据，再从 DB 查完整历史
        List<MessageEntity> entityList = messageMapper.listBySessionId(sessionId);
        if (entityList == null || entityList.isEmpty()) {
            return List.of();
        }

        List<ChatMessageDTO> dtoList = entityList.stream()
                .map(ChatMessageDTO::fromEntity)
                .toList();

        // 3. 回填一份到 Redis，方便下次直接命中
        backfillRedis(key, dtoList);

        return dtoList;
    }

    private ChatMessageDTO toDtoQuietly(String json) {
        try {
            return objectMapper.readValue(json, ChatMessageDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    private void backfillRedis(String key, List<ChatMessageDTO> dtoList) {
        if (dtoList.isEmpty()) return;

        // 只取最后 MAX_RECENT_MESSAGES 条，保证是“最近几条”
        int size = dtoList.size();
        int start = Math.max(0, size - MAX_RECENT_MESSAGES);
        List<ChatMessageDTO> recent = dtoList.subList(start, size);

        stringRedisTemplate.delete(key);
        for (ChatMessageDTO dto : recent) {
            try {
                String json = objectMapper.writeValueAsString(dto);
                stringRedisTemplate.opsForList().rightPush(key, json);
            } catch (JsonProcessingException e) {
                // 单条出错就略过
            }
        }
        stringRedisTemplate.expire(key, Duration.ofDays(7));
    }

}
