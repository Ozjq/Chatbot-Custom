package com.zjq.chatbot.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionEntity {
    /** id BIGINT PRIMARY KEY AUTO_INCREMENT */
    private Long id;

    /** anon_id CHAR(36) NOT NULL */
    private String anonId;

    /** title VARCHAR(200) */
    private String title;

    /** channel VARCHAR(32) DEFAULT 'web' */
    private String channel;

    /** message_count INT DEFAULT 0 */
    private Integer messageCount;

    /** last_message_at DATETIME */
    private LocalDateTime lastMessageAt;

    /** created_at DATETIME DEFAULT CURRENT_TIMESTAMP */
    private LocalDateTime createdAt;

    /** updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP */
    private LocalDateTime updatedAt;
}
