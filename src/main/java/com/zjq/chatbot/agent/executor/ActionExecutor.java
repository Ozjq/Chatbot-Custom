package com.zjq.chatbot.agent.executor;

import com.zjq.chatbot.agent.model.AgentAction;
import com.zjq.chatbot.agent.model.AgentContext;
import com.zjq.chatbot.agent.model.AgentObservation;
import com.zjq.chatbot.service.AiAbilityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ActionExecutor {

    @Resource
    private AiAbilityService aiAbilityService;

    /**
     * 所有“强制执行工具”的注册表
     */
    private final Map<String, DirectToolExecutor> executorMap;

    public ActionExecutor(List<DirectToolExecutor> executors) {
        this.executorMap = executors.stream()
                .collect(Collectors.toMap(
                        DirectToolExecutor::supportToolName,
                        Function.identity()
                ));
    }

    public AgentObservation execute(AgentAction action, AgentContext context) {
        if (action == null || action.getType() == null) {
            return AgentObservation.fail("system", "动作为空");
        }

        try {
            return switch (action.getType()) {
                case DIRECT_ANSWER -> executeDirectAnswer(action, context);
                case RAG_SEARCH -> executeRag(context);
                case CALL_TOOL -> executeTool(action, context);
                case FINISH -> AgentObservation.success("system", "结束", "完成");
            };
        } catch (Exception e) {
            return AgentObservation.fail("system", "执行异常：" + e.getMessage());
        }
    }

    private AgentObservation executeDirectAnswer(AgentAction action, AgentContext context) {
        String result = aiAbilityService.doChat(
                action.getDirectAnswer() != null ? action.getDirectAnswer() : context.getUserInput(),
                context.getSessionId()
        );
        return AgentObservation.success("model", result, "生成答案成功");
    }

    private AgentObservation executeRag(AgentContext context) {
        String result = aiAbilityService.chatWithRag(
                context.getUserInput(),
                context.getSessionId()
        );
        return AgentObservation.success("rag", result, "RAG完成");
    }

    private AgentObservation executeTool(AgentAction action, AgentContext context) {

        // ⭐ 优先走“强制工具执行”
        DirectToolExecutor executor = executorMap.get(action.getToolName());
        if (executor != null) {
            return executor.execute(action, context);
        }

        // ⭐ 否则交给 LLM 自由调用
        String prompt = buildToolPrompt(action, context);
        String result = aiAbilityService.reactToolChat(prompt, context.getSessionId());

        return AgentObservation.success("tool", result, "LLM工具调用完成");
    }

    private String buildToolPrompt(AgentAction action, AgentContext context) {
        return """
                请调用合适工具完成任务
                
                工具类型建议：%s
                用户需求：%s
                输入：%s
                """.formatted(
                action.getToolName(),
                context.getUserInput(),
                action.getToolInput()
        );
    }
}