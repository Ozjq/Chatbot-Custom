package com.zjq.chatbot.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Bean
    public ToolCallback[] allTools() {
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        FileOperationTool fileOperationTool = new FileOperationTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();

        return ToolCallbacks.from(
                pdfGenerationTool,
                fileOperationTool,
                resourceDownloadTool);
    }
}
