package com.zjq.chatbot.config;

import com.zjq.chatbot.tools.FileOperationTool;
import com.zjq.chatbot.tools.PDFGenerationTool;
import com.zjq.chatbot.tools.ResourceDownloadTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Bean
    public PDFGenerationTool pdfGenerationTool() {
        return new PDFGenerationTool();
    }

    @Bean
    public FileOperationTool fileOperationTool() {
        return new FileOperationTool();
    }

    @Bean
    public ResourceDownloadTool resourceDownloadTool() {
        return new ResourceDownloadTool();
    }

    /**
     * 只保留允许 LLM 自由调用的工具
     */
    @Bean
    public ToolCallback[] allTools(
            FileOperationTool fileOperationTool,
            ResourceDownloadTool resourceDownloadTool
    ) {
        return ToolCallbacks.from(
                fileOperationTool,
                resourceDownloadTool
        );
    }
}