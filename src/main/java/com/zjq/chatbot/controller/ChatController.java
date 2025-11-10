package com.zjq.chatbot.controller;

import com.zjq.chatbot.app.Chatbot;
import com.zjq.chatbot.entity.MessageEntity;
import com.zjq.chatbot.service.ChatService;
import com.zjq.chatbot.service.SessionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 聊天界面相关接口
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final Chatbot chatbot;

    @Resource
    private ChatService chatService;

    private SessionService sessionService;

    public ChatController(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    public record ChatReq(String chatId, String message) {}
    public record ChatResp(String chatId, String answer) {}

    @PostMapping("/v1")
    public ChatResp send(@RequestBody ChatReq req) {
        // 首次没有 chatId 就给个新的（先用随机UUID；以后换成持久化sessionId）
        String cid = (req.chatId()==null || req.chatId().isBlank()) ? java.util.UUID.randomUUID().toString() : req.chatId();
        String answer = chatbot.doChat(req.message(), cid);
        return new ChatResp(cid, answer);
    }

    @PostMapping("/v2")
    public void sendV2(@RequestBody ChatReq req){
        String cid = (req.chatId()==null || req.chatId().isBlank()) ? java.util.UUID.randomUUID().toString() : req.chatId();
        MessageEntity message = MessageEntity.builder()
                .content(req.message())
                .sessionId(12L)
                .role(MessageEntity.Role.user)
                .createdAt(LocalDateTime.now())
                .build();
        chatService.insert(message);

    }

}
