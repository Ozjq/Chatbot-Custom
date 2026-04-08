package com.zjq.chatbot.tools;

import com.zjq.chatbot.tools.FileOperationTool;
import com.zjq.chatbot.tools.PDFGenerationTool;
import com.zjq.chatbot.tools.ResourceDownloadTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${search-api.api-key}")
    private String searchApiKey;

    /**
     * 只保留允许 LLM 自由调用的工具
     */
    @Bean
    public ToolCallback[] allTools(
            FileOperationTool fileOperationTool,
            ResourceDownloadTool resourceDownloadTool
    ) {
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ImageSearchTool imageSearchTool = new ImageSearchTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        TerminateTool terminateTool = new TerminateTool();
        return ToolCallbacks.from(
                fileOperationTool,
                resourceDownloadTool,
                webSearchTool,
                imageSearchTool,
                webScrapingTool,
                terminalOperationTool,
                terminateTool
        );
    }
}