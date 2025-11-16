package com.zjq.chatbot.rag.etl;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.SummaryMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于ai的文档原信息增强器
 */
@Component
public class MyKeywordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;


    // 关键词元信息增强器
    public List<Document> enrichDocumentsByKeyword(List<Document> documents) {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
        return enricher.apply(documents);
    }

    // 摘要元信息增强器
    public List<Document> enrichDocumentsBySummary(List<Document> documents) {
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(dashscopeChatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.NEXT));
        return enricher.apply(documents);
    }

}
