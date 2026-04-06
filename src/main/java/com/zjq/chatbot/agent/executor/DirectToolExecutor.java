package com.zjq.chatbot.agent.executor;

import com.zjq.chatbot.agent.model.AgentAction;
import com.zjq.chatbot.agent.model.AgentContext;
import com.zjq.chatbot.agent.model.AgentObservation;

public interface DirectToolExecutor {

    /**
     * 支持的工具名称（必须和 toolName 一致）
     */
    String supportToolName();

    /**
     * 执行工具
     */
    AgentObservation execute(AgentAction action, AgentContext context);
}