package com.zjq.chatbot.agent;

import com.zjq.chatbot.agent.executor.ActionExecutor;
import com.zjq.chatbot.agent.model.AgentAction;
import com.zjq.chatbot.agent.model.AgentContext;
import com.zjq.chatbot.agent.model.AgentObservation;
import com.zjq.chatbot.agent.reasoner.ReasoningEngine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class AgentOrchestrator {

    @Resource
    private ReasoningEngine reasoningEngine;

    @Resource
    private ActionExecutor actionExecutor;

    public AgentContext run(String userInput, String sessionId) {
        AgentContext context = AgentContext.init(userInput, sessionId);
        context.setThought(buildInitialThought(userInput));

        while (!context.isFinished() && context.getRound() < context.getMaxRounds()) {
            context.nextRound();
            log.info("Agent round {} started, sessionId={}", context.getRound(), sessionId);

            AgentAction action = reasoningEngine.decide(context);
            context.addAction(action);
            log.info("Agent action: {}", action);

            if (action == null || action.getType() == AgentAction.ActionType.FINISH) {
                context.setFinished(true);
                break;
            }

            AgentObservation observation = actionExecutor.execute(action, context);
            context.addObservation(observation);
            log.info("Agent observation: {}", observation);

            if (observation.isSuccess()) {
                switch (action.getType()) {
                    case DIRECT_ANSWER, RAG_SEARCH -> {
                        if (StringUtils.hasText(observation.getContent())) {
                            context.setAnswerDraft(observation.getContent());
                            context.setAnswerReady(true);
                        }
                    }
                    case CALL_TOOL -> {
                        if ("pdfGenerationTool".equals(action.getToolName())
                                && StringUtils.hasText(observation.getContent())) {
                            context.setPdfResult(observation.getContent());
                            context.setPdfReady(true);
                        }
                    }
                    default -> {
                    }
                }
            } else {
                log.warn("Agent step failed: {}", observation.getErrorMessage());
            }

            // 只有答案完成，并且不需要 PDF 或 PDF 已完成，才能结束
            if (context.isAnswerReady() && (!context.isNeedPdf() || context.isPdfReady())) {
                context.setFinished(true);
            }
        }

        buildFinalAnswer(context);
        log.info("Agent finalAnswer: {}", context.getFinalAnswer());
        return context;
    }

    private void buildFinalAnswer(AgentContext context) {
        if (context.isAnswerReady()) {
            if (context.isNeedPdf()) {
                if (context.isPdfReady()) {
                    context.setFinalAnswer(context.getAnswerDraft() +
                            "\n\nPDF已生成：" + context.getPdfResult());
                } else {
                    context.setFinalAnswer(context.getAnswerDraft() +
                            "\n\nPDF生成失败或未完成。");
                }
            } else {
                context.setFinalAnswer(context.getAnswerDraft());
            }
        } else {
            context.setFinalAnswer("已完成任务分析，但暂时无法生成明确结果。");
        }
    }

    private String buildInitialThought(String userInput) {
        return """
                任务目标：%s
                初始策略：
                1. 先生成正文答案
                2. 若用户要求 PDF，则基于正文生成 PDF
                3. 所有任务完成后输出最终答案
                """.formatted(userInput);
    }
}