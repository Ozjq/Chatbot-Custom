package com.zjq.chatbot.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageEntity {
    /** id BIGINT PRIMARY KEY AUTO_INCREMENT */
    //消息唯一id
    private Long id;

    /** session_id BIGINT NOT NULL */
    //所属会话id
    private Long sessionId;

    /** role ENUM('user','assistant','system','tool') NOT NULL */
    private Role role;

    /** content MEDIUMTEXT NOT NULL */
    //消息正文
    private String content;

    /** meta_json JSON -> 先用 String 存 JSON 文本，省去自定义 TypeHandler */
    //扩展信息
    private String metaJson;

    /** created_at DATETIME DEFAULT CURRENT_TIMESTAMP */
    //消息写入时间
    private LocalDateTime createdAt;

    /** 角色枚举，与表结构保持一致 */
    public enum Role {
        user, assistant, system, tool
    }
}
