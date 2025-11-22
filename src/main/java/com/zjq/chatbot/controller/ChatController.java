package com.zjq.chatbot.controller;

import com.zjq.chatbot.app.Chatbot;
import com.zjq.chatbot.entity.ChatMessageDTO;
import com.zjq.chatbot.entity.MessageEntity;
import com.zjq.chatbot.entity.SessionEntity;
import com.zjq.chatbot.service.ChatSearchService;
import com.zjq.chatbot.service.ChatService;
import com.zjq.chatbot.service.SessionService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
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
    @Resource
    private ChatSearchService chatSearchService;

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

    @GetMapping("/init")
    public List<SessionEntity> init() {
        return sessionService.listRecent(10);
    }

    @PostMapping("/v1")
    public ChatResp ragChat(@RequestBody ChatReq req) {
        Long sid = (req.sessionId() == null) ? sessionService.create() : req.sessionId();

        // 1 发送请求给 bot,插入数据库
        MessageEntity message = MessageEntity.builder()
                .content(req.message())
                .sessionId(sid)
                .role(MessageEntity.Role.user)
                .createdAt(LocalDateTime.now())
                .build();
        chatService.insert(message);
        sessionService.messageAppend(sid);

        // 2 bot返回响应
        String answer = chatbot.chatWithRag(req.message(), String.valueOf(sid));

        //3 写入system返回消息
        chatService.insert(MessageEntity.builder()
                .sessionId(sid)
                .role(MessageEntity.Role.system)
                .content(answer)
                .metaJson(null)
                .createdAt(LocalDateTime.now())
                .build());
        sessionService.messageAppend(sid);

        try {
            chatSearchService.indexSession(sid);
        } catch (Exception e) {
            System.out.println("index ES failed");
        }

        return new ChatResp(answer,sid);
    }

    @PostMapping("/v2")
    public ChatResp sendV2(@RequestBody ChatReq req) {
        Long sid = (req.sessionId() == null) ? sessionService.create() : req.sessionId();

        // 1 发送请求给 bot,插入数据库
        MessageEntity message = MessageEntity.builder()
                .content(req.message())
                .sessionId(sid)
                .role(MessageEntity.Role.user)
                .createdAt(LocalDateTime.now())
                .build();
        chatService.insert(message);
        sessionService.messageAppend(sid);

        // 2 bot返回响应
        String answer = chatbot.doChat(req.message(), String.valueOf(sid));

        //3 写入system返回消息
        chatService.insert(MessageEntity.builder()
                .sessionId(sid)
                .role(MessageEntity.Role.system)
                .content(answer)
                .metaJson(null)
                .createdAt(LocalDateTime.now())
                .build());
        sessionService.messageAppend(sid);

        try {
            chatSearchService.indexSession(sid);
        } catch (Exception e) {
            System.out.println("index ES failed");
        }

        return new ChatResp(answer,sid);
    }

    /** 查询某会话下的所有消息
     * 要么用路径变量：@GetMapping("/messages/{sessionId}") + @PathVariable Long sessionId，请求 /messages/3
     * 要么用查询参数：@GetMapping("/messages") + @RequestParam Long sessionId，请求 /messages?sessionId=3
     * 二选一不要混用
     * @param sessionId
     * @return
     */
    @GetMapping("/messages")
    public List<ChatMessageDTO> getMessagesBySid(@RequestParam("sessionId") Long sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        return chatService.getMessagesBySid(sessionId);
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
