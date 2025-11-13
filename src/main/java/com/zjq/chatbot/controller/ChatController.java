package com.zjq.chatbot.controller;

import com.zjq.chatbot.app.Chatbot;
import com.zjq.chatbot.entity.MessageEntity;
import com.zjq.chatbot.entity.SessionEntity;
import com.zjq.chatbot.service.ChatService;
import com.zjq.chatbot.service.SessionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天界面相关接口
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final Chatbot chatbot;

    @Resource
    private ChatService chatService;

    @Resource
    private SessionService sessionService;

    public ChatController(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    public record ChatReq(String message, Long sessionId) {}
    public record ChatResp(String answer, Long sessionId) {}

//    @PostMapping("/v1")
//    public ChatResp send(@RequestBody ChatReq req) {
//        // 首次没有 chatId 就给个新的（先用随机UUID；以后换成持久化sessionId）
//        String cid = (req.chatId()==null || req.chatId().isBlank()) ? java.util.UUID.randomUUID().toString() : req.chatId();
//        String answer = chatbot.doChat(req.message(), cid);
//        return new ChatResp(cid, answer);
//    }

    @PostMapping("/init")
    public List<SessionEntity> init() {
        return sessionService.listRecent(10);
    }

    @PostMapping("/v2")
    public ChatResp sendV2(@RequestBody ChatReq req) {
        Long sid = (req.sessionId() == null) ? sessionService.create() : req.sessionId();

        // 发送请求给 bot,插入数据库
        MessageEntity message = MessageEntity.builder()
                .content(req.message())
                .sessionId(sid)
                .role(MessageEntity.Role.user)
                .createdAt(LocalDateTime.now())
                .build();
        chatService.insert(message);
        // bot返回响应
        String answer = chatbot.doChat(req.message(), String.valueOf(sid));

        return new ChatResp(answer,sid);
    }

    /**
     * 可选：显式创建会话（前端也可以先调用这个拿到 sessionId 再发消息）
     * POST /api/chat/v2/sessions
     */
    @PostMapping("/sessions")
    public Long createSession() {
        return sessionService.create();
    }
}
