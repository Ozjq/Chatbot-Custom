package com.zjq.chatbot.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(indexName = "chat_conversation")
public class ChatESEntity {

    @Id
    private Long sessionId;

//    @Field(type = FieldType.Keyword)
//    private String userId;

    @Field(type = FieldType.Date)
    private LocalDate startTime;

    @Field(type = FieldType.Date)
    private LocalDate endTime;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String channel;

    // 这里的 analyzer 名称要和你 ES 里的 mapping 对上
    //@Field(type = FieldType.Text)
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String fullText;


}
