package com.zjq.chatbot.agent.executor;

import com.zjq.chatbot.agent.model.AgentAction;
import com.zjq.chatbot.agent.model.AgentContext;
import com.zjq.chatbot.agent.model.AgentObservation;
import com.zjq.chatbot.service.AiAbilityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ActionExecutor {

    @Resource
    private AiAbilityService aiAbilityService;

    public AgentObservation execute(AgentAction action, AgentContext context) {
        if (action == null || action.getType() == null) {
            return AgentObservation.fail("system", "动作为空，无法执行");
        }

        try {
            return switch (action.getType()) {
                case DIRECT_ANSWER -> executeDirectAnswer(action, context);
                case RAG_SEARCH -> executeRagSearch(context);
                case CALL_TOOL -> executeToolCall(action, context);
                case FINISH -> AgentObservation.success("system", "流程结束", "无需继续执行");
            };
        } catch (Exception e) {
            return AgentObservation.fail("system", "动作执行异常：" + e.getMessage());
        }
    }

    private AgentObservation executeDirectAnswer(AgentAction action, AgentContext context) {
        String input = StringUtils.hasText(action.getDirectAnswer())
                ? action.getDirectAnswer()
                : context.getUserInput();

        String result = aiAbilityService.doChat(input, context.getSessionId());
        return AgentObservation.success("model", result, "模型已生成正文答案");
    }

    private AgentObservation executeRagSearch(AgentContext context) {
        String result = aiAbilityService.chatWithRag(context.getUserInput(), context.getSessionId());
        return AgentObservation.success("rag", result, "已完成知识增强答案生成");
    }

    private AgentObservation executeToolCall(AgentAction action, AgentContext context) {
        if ("pdfGenerationTool".equals(action.getToolName())) {
            String content = action.getToolInput();

            if (!StringUtils.hasText(content)) {
                return AgentObservation.fail("tool", "PDF正文为空，无法生成PDF");
            }

            String pdfPrompt = buildPdfPrompt(content);
            String result = aiAbilityService.reactToolChat(pdfPrompt, context.getSessionId());

            return AgentObservation.success("tool", result, "PDF生成工具执行完成");
        }

        String toolPrompt = buildToolPrompt(action, context);
        String result = aiAbilityService.reactToolChat(toolPrompt, context.getSessionId());
        return AgentObservation.success("tool", result, "工具调用完成");
    }

    private String buildPdfPrompt(String content) {
        return """
                请务必调用 pdfGenerationTool 生成一个 PDF 文件。
                
                要求：
                1. 标题：生成的文档
                2. 将下面正文完整写入 PDF
                3. 不要省略正文内容
                4. 如果生成成功，请返回生成结果或文件路径
                
                正文内容：
                %s
                """.formatted(content);
    }

    private String buildToolPrompt(AgentAction action, AgentContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("请根据以下任务优先调用合适工具完成处理。\n");
        sb.append("工具类型建议：").append(action.getToolName()).append("\n");
        sb.append("用户原始需求：").append(context.getUserInput()).append("\n");
        if (StringUtils.hasText(action.getToolInput())) {
            sb.append("本轮工具输入：").append(action.getToolInput()).append("\n");
        }
        sb.append("执行后请基于工具结果给出最终答案。");
        return sb.toString();
    }
}