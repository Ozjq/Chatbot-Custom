package com.zjq.chatbot.controller;

import com.zjq.chatbot.app.Chatbot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final Chatbot chatbot;

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

}
