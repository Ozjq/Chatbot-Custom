package com.zjq.chatbot.controller;

import com.zjq.chatbot.entity.ChatESConversation;
import com.zjq.chatbot.service.ChatSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatSearchController {

    private final ChatSearchService chatSearchService;

    @GetMapping("/api/chat/search")
    public Page<ChatESConversation> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return chatSearchService.searchConversations(keyword, page, size);
    }
}
