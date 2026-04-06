package com.zjq.chatbot.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动作执行后的观察结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentObservation {

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 结果来源
     * 例如：tool / rag / model / system
     */
    private String source;

    /**
     * 原始结果内容
     */
    private String content;

    /**
     * 简短摘要
     */
    private String summary;

    /**
     * 错误信息
     */
    private String errorMessage;

    public static AgentObservation success(String source, String content, String summary) {
        return AgentObservation.builder()
                .success(true)
                .source(source)
                .content(content)
                .summary(summary)
                .build();
    }

    public static AgentObservation fail(String source, String errorMessage) {
        return AgentObservation.builder()
                .success(false)
                .source(source)
                .errorMessage(errorMessage)
                .summary(errorMessage)
                .build();
    }
}