package com.zjq.chatbot.service;

import com.zjq.chatbot.entity.ChatESEntity;
import com.zjq.chatbot.entity.ChatMessageDTO;
import com.zjq.chatbot.repository.ChatESRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatSearchService {

    @Resource
    private ChatESRepository esRepository;

    @Resource
    private ChatService chatService;


    public Page<ChatESEntity> searchConversations(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return esRepository.searchByFullText(keyword, pageable);
    }

    public void indexSession(Long sessionId) {

        List<ChatMessageDTO> messages = chatService.getMessagesBySid(sessionId);
        if (messages == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (ChatMessageDTO m : messages) {
            sb.append(m.getRole()).append(": ")
                    .append(m.getContent()).append("\n");
        }

        LocalDateTime start = messages.get(0).getCreatedAt();
        LocalDateTime end = messages.get(messages.size()-1).getCreatedAt();

        ChatESEntity conversation = new ChatESEntity();
        conversation.setSessionId(sessionId);
        conversation.setStartTime(LocalDate.from(start));
        conversation.setEndTime(LocalDate.from(end));
        conversation.setFullText(sb.toString());

        esRepository.save(conversation);
    }
}

