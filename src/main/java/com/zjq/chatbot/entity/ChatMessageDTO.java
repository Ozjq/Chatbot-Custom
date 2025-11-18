package com.zjq.chatbot.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {

    private String role;           // "user" / "assistant"
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageDTO fromEntity(MessageEntity entity) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setRole(String.valueOf(entity.getRole()));
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}

