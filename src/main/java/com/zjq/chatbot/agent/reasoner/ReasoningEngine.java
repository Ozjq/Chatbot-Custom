package com.zjq.chatbot.agent.reasoner;

import com.zjq.chatbot.agent.model.AgentAction;
import com.zjq.chatbot.agent.model.AgentContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ReasoningEngine {

    public AgentAction decide(AgentContext context) {
        if (context.isFinished() || context.getRound() >= context.getMaxRounds()) {
            return finish("达到最大执行轮数，结束流程");
        }

        String userInput = safe(context.getUserInput()).toLowerCase();

        /**
         * 第一阶段：先产出答案正文
         */
        if (!context.isAnswerReady()) {
            if (containsAny(userInput, "知识库", "文档", "资料", "杭电", "项目内容", "根据文档", "根据资料")) {
                return AgentAction.builder()
                        .type(AgentAction.ActionType.RAG_SEARCH)
                        .reason("先通过知识库或上下文生成正文答案")
                        .build();
            }

            return AgentAction.builder()
                    .type(AgentAction.ActionType.DIRECT_ANSWER)
                    .directAnswer(context.getUserInput())
                    .reason("先生成正文答案")
                    .build();
        }

        /**
         * 第二阶段：PDF 失败熔断（防止重复重试）
         */
        if (context.isNeedPdf() && context.isPdfFailed()) {
            return finish("PDF生成失败，触发熔断，结束流程");
        }

        /**
         * 第三阶段：如果需要 PDF，且尚未成功且尚未尝试过，则生成 PDF
         */
        if (context.isNeedPdf() && !context.isPdfReady() && !context.isPdfTried()) {
            return AgentAction.builder()
                    .type(AgentAction.ActionType.CALL_TOOL)
                    .toolName("documentExportTool")
                    .toolInput(context.getAnswerDraft())
                    .reason("正文答案已准备完成，开始生成文档")
                    .build();
        }

        /**
         * 第四阶段：全部完成
         */
        return finish("答案和附加任务均已完成");
    }

    private AgentAction finish(String reason) {
        return AgentAction.builder()
                .type(AgentAction.ActionType.FINISH)
                .reason(reason)
                .build();
    }

    private boolean containsAny(String text, String... keywords) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }
}