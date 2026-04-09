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
            log.info("Agent observation: success={}, source={}, summary={}, err={}",
                    observation.isSuccess(),
                    observation.getSource(),
                    observation.getSummary(),
                    observation.getErrorMessage());

            if (observation.isSuccess()) {
                switch (action.getType()) {
                    case DIRECT_ANSWER, RAG_SEARCH -> {
                        if (StringUtils.hasText(observation.getContent())) {
                            context.setAnswerDraft(observation.getContent());
                            context.setAnswerReady(true);
                        }
                    }
                    case CALL_TOOL -> {
                        // 将条件放宽，包含你现在生成文件可能用到的工具名
                        String toolName = action.getToolName();
                        if ("pdfGenerationTool".equals(toolName)
                                || "documentExportTool".equals(toolName)
                                || "fileOperationTool".equals(toolName)) {

                            context.setPdfTried(true);
                            context.setPdfFailed(false);
                            if (StringUtils.hasText(observation.getContent())) {
                                context.setPdfResult(observation.getContent());
                                // 只要这些工具执行成功，就认为文档准备好了
                                context.setPdfReady(true);
                            }
                        }
                    }
                    default -> {
                    }
                }
            } else {
                log.warn("Agent step failed: {}", observation.getErrorMessage());

                // PDF 工具失败 -> 熔断，不再重复重试
                if (action.getType() == AgentAction.ActionType.CALL_TOOL
                        && "pdfGenerationTool".equals(action.getToolName())) {
                    context.setPdfTried(true);
                    context.setPdfFailed(true);
                    context.setFinished(true);
                }
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
            if (context.isNeedPdf()) { // 这里的 isNeedPdf 实际上现在代表 "是否需要导出文档"
                if (context.isPdfReady()) {
                    context.setFinalAnswer(context.getAnswerDraft() +
                            "\n\n✅ 文档已成功导出至本地：" + context.getPdfResult());
                } else if (context.isPdfFailed()) {
                    context.setFinalAnswer(context.getAnswerDraft() +
                            "\n\n❌ 文档导出失败（已熔断停止重试），请检查路径或权限配置。");
                } else {
                    context.setFinalAnswer(context.getAnswerDraft() +
                            "\n\n❌ 文档导出失败或未完成。");
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
                1. 你的首要任务是**生成完整、详尽、高质量的正文答案**（必须包含完整的路线规划、步骤细节、景点介绍等）。绝对不能只回复“任务已完成”或“请查收文档”等敷衍的提示语，必须将实质性的完整指南内容作为你的直接输出！
                2. ⚠️ 强烈警告：如果用户需要看图片，【绝对禁止】使用 imgur、unsplash 等捏造的假链接！你必须且只能调用 `imageSearchTool` 工具获取真实的图片 URL，并将图片使用 Markdown 语法 `![图片描述](真实URL)` 穿插在你生成的长文指南中。
                3. 若用户要求导出、生成文件(如PDF/MD/HTML)，你**无需理会**，绝对不要在回答中提及任何转换、导出的教程或说明。我们的系统会自动在后台提取你输出的这篇“完整正文”并导出为文件。
                4. 总结：你现在的唯一职责，就是输出一篇**完整排版、图文并茂的 Markdown 长篇游记/指南**。
                """.formatted(userInput);
    }
}