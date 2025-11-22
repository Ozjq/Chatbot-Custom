package com.zjq.chatbot.repository;

import com.zjq.chatbot.entity.ChatESEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChatESRepository
        extends ElasticsearchRepository<ChatESEntity, String> {

    Page<ChatESEntity> findByFullTextContaining(String keyword, Pageable pageable);
}

