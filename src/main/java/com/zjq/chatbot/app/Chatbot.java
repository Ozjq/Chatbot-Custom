package com.zjq.chatbot.app;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Chatbot {
    private final ChatClient chatClient;

    public Chatbot(@Qualifier("dashscopeChatModel") ChatModel dashScopeChatModel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(
                    new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
}
