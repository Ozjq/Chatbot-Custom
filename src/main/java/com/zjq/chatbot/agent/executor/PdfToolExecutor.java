package com.zjq.chatbot.agent.executor;

import com.zjq.chatbot.agent.model.AgentAction;
import com.zjq.chatbot.agent.model.AgentContext;
import com.zjq.chatbot.agent.model.AgentObservation;
import com.zjq.chatbot.tools.PDFGenerationTool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PdfToolExecutor implements DirectToolExecutor {

    @Resource
    private PDFGenerationTool pdfGenerationTool;

    @Override
    public String supportToolName() {
        return "pdfGenerationTool";
    }

    @Override
    public AgentObservation execute(AgentAction action, AgentContext context) {
        String content = action.getToolInput();

        if (!StringUtils.hasText(content)) {
            return AgentObservation.fail("tool", "PDF正文为空，无法生成PDF");
        }

        // ⭐ 清洗内容（非常重要）
        content = cleanContent(content);

        // ⭐ 强制使用 pdf 文件名（避免生成 txt）
        String fileName = "生成的文档_" + System.currentTimeMillis() + ".pdf";

        String result = pdfGenerationTool.generatePDF(fileName, content);

        if (result != null && result.startsWith("Error")) {
            return AgentObservation.fail("tool", result);
        }

        return AgentObservation.success("tool", result, "PDF生成成功");
    }

    /**
     * 清理模型中无用描述
     */
    private String cleanContent(String content) {
        String cleaned = content;

        // 删除“不能生成PDF”等误导语句
        cleaned = cleaned.replaceAll("(?s)❌ 关于生成PDF：.*", "");
        cleaned = cleaned.replaceAll("(?s)⚠️ 关于生成PDF：.*", "");
        cleaned = cleaned.replaceAll("(?s)是否需要我.*生成.*", "");

        return cleaned.trim();
    }
}