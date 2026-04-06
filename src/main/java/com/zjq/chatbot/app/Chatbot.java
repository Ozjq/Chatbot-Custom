package com.zjq.chatbot.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjq.chatbot.agent.AgentOrchestrator;
import com.zjq.chatbot.chatmemory.FileBasedChatMemory;
import com.zjq.chatbot.service.AiAbilityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Component
public class Chatbot {

    @Resource
    private ChatClient chatClient;

    @Resource
    private AiAbilityService aiAbilityService;

    @Resource
    private AgentOrchestrator agentOrchestrator;

    public String doChat(String message, String sessionId) {
        return aiAbilityService.doChat(message, sessionId);
    }

    public String chatWithRag(String message, String sessionId) {
        return aiAbilityService.chatWithRag(message, sessionId);
    }

    public String reactChat(String message, String sessionId) {
        return aiAbilityService.reactToolChat(message, sessionId);
    }

    public String reactChatV2(String message, String sessionId) {
        return agentOrchestrator.run(message, sessionId).getFinalAnswer();
    }
}
