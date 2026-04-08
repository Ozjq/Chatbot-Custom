package com.zjq.chatbot.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class AiAbilityService {

    @Resource
    private ChatClient chatClient;

    @Resource
    private VectorStore simpleVectorStore;

    @Resource
    private ToolCallback[] allTools;
//
//    @Resource
//    private ToolCallbackProvider toolCallbackProvider;

    public String doChat(String message, String sessionId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        return response.getResult().getOutput().getText();
    }

    public String chatWithRag(String message, String sessionId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new QuestionAnswerAdvisor(simpleVectorStore))
                .call()
                .chatResponse();
        return response.getResult().getOutput().getText();
    }

    public String reactToolChat(String message, String sessionId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new QuestionAnswerAdvisor(simpleVectorStore))
                .tools(allTools)
                .call()
                .chatResponse();
        return response.getResult().getOutput().getText();
    }

//    public String doChatWithMcp(String message, String sessionId) {
//        ChatResponse response = chatClient
//                .prompt()
//                .user(message)
//                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId)
//                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .tools(toolCallbackProvider)
//                .call()
//                .chatResponse();
//        String content = response.getResult().getOutput().getText();
//        return content;
//    }
}