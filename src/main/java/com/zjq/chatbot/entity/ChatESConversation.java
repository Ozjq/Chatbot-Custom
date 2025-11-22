package com.zjq.chatbot.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Data
@Document(indexName = "chat_conversation")
public class ChatESConversation {

    @Id
    private String sessionId;

//    @Field(type = FieldType.Keyword)
//    private String userId;

    @Field(type = FieldType.Date)
    private Instant startTime;

    @Field(type = FieldType.Date)
    private Instant endTime;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String channel;

    // 这里的 analyzer 名称要和你 ES 里的 mapping 对上
    //@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    @Field(type = FieldType.Text)
    private String fullText;
}
