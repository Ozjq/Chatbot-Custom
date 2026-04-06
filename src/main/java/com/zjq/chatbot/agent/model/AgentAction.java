package com.zjq.chatbot.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 下一步动作定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentAction {

    /**
     * 动作类型
     */
    private ActionType type;

    /**
     * 工具名称（当 type = CALL_TOOL 时使用）
     */
    private String toolName;

    /**
     * 工具输入参数（可直接传给工具或作为工具提示词）
     */
    private String toolInput;

    /**
     * 动作原因说明（内部使用，不直接暴露给用户）
     */
    private String reason;

    /**
     * 如果是直接回答，这里可以放回答草稿
     */
    private String directAnswer;

    public enum ActionType {
        /**
         * 直接回答
         */
        DIRECT_ANSWER,

        /**
         * 调用工具
         */
        CALL_TOOL,

        /**
         * 使用知识库检索 / RAG
         */
        RAG_SEARCH,

        /**
         * 结束流程
         */
        FINISH
    }
}