package com.zjq.chatbot.service;

import com.zjq.chatbot.entity.ChatESConversation;
import com.zjq.chatbot.repository.ChatConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSearchService {

    private final ChatConversationRepository conversationRepository;

    public Page<ChatESConversation> searchConversations(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return conversationRepository.findByFullTextContaining(keyword, pageable);
    }
}

