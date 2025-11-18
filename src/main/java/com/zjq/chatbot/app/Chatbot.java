package com.zjq.chatbot.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjq.chatbot.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Component
public class Chatbot {
    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT = "扮演智能客服机器人，回答用户问题";

    public Chatbot(@Qualifier("dashscopeChatModel") ChatModel dashScopeChatModel) {
        //基于内存对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        //基于文件的ChatMemory
        String fileDir = System.getProperty("user.dir")+"/tmp/chat-memory";
        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(fileDir);

        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                    new MessageChatMemoryAdvisor(chatMemory)  //对话记忆功能，将其作为消息集合添加到提示词中
                )
                .build();
    }

    public String doChat(String message,String chatId){
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY,chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY,10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }

    @Resource
    VectorStore simpleVectorStore;

    public String chatWithRag(String message, String sessionId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY,sessionId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY,10))
                .advisors(new QuestionAnswerAdvisor(simpleVectorStore)) //适用于 “我有知识库（向量化文档）＋我想要问答” 的场景
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }
}
