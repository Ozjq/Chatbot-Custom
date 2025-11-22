package com.zjq.chatbot.repository;

import com.zjq.chatbot.entity.ChatESConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChatConversationRepository
        extends ElasticsearchRepository<ChatESConversation, String> {

    Page<ChatESConversation> findByFullTextContaining(String keyword, Pageable pageable);
}

