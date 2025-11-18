package com.zjq.chatbot.rag;

import com.zjq.chatbot.rag.etl.MyKeywordEnricher;
import com.zjq.chatbot.rag.etl.MyTokenTextSplitter;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SimpleVectorStoreConfig {
    @Resource
    DocumentLoader documentLoader;
    @Resource
    MyTokenTextSplitter splitter;
    @Resource
    MyKeywordEnricher myEnricher;

    @Bean
    VectorStore simpleVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        //抽取 Extract
        List<Document> documentList = documentLoader.loadMarkDowns();

        //转换 Transform
//        List<Document> splitDocuments = splitter.splitDocuments(documentList);
//        List<Document> enrichDocuments = myEnricher.enrichDocumentsByKeyword(splitDocuments);
//
//        //加载 Load
//        simpleVectorStore.write(enrichDocuments);
        simpleVectorStore.add(documentList);
        return simpleVectorStore;
    }
}
