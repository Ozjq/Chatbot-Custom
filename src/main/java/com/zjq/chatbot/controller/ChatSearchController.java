package com.zjq.chatbot.controller;

import com.zjq.chatbot.entity.ChatESEntity;
import com.zjq.chatbot.service.ChatSearchService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatSearchController {

    @Resource
    private ChatSearchService chatSearchService;


    @GetMapping("/search")
    public Page<ChatESEntity> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return chatSearchService.searchConversations(keyword, page, size);
    }
}
