package com.zjq.chatbot.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentContext {

    private String userInput;
    private String sessionId;

    @Builder.Default
    private int round = 0;

    @Builder.Default
    private int maxRounds = 5;

    @Builder.Default
    private boolean finished = false;

    private String finalAnswer;
    private String thought;

    @Builder.Default
    private List<AgentAction> actions = new ArrayList<>();

    @Builder.Default
    private List<AgentObservation> observations = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * ===== 状态字段 =====
     */
    @Builder.Default
    private boolean needPdf = false;

    @Builder.Default
    private boolean answerReady = false;

    @Builder.Default
    private boolean pdfReady = false;

    // 新增：是否已经尝试过PDF生成
    @Builder.Default
    private boolean pdfTried = false;

    // 新增：PDF是否失败（失败熔断）
    @Builder.Default
    private boolean pdfFailed = false;

    /**
     * 第一阶段生成的正文答案
     */
    private String answerDraft;

    /**
     * 生成后的 PDF 路径或结果信息
     */
    private String pdfResult;

    public static AgentContext init(String userInput, String sessionId) {
        return AgentContext.builder()
                .userInput(userInput)
                .sessionId(sessionId)
                .round(0)
                .maxRounds(5)
                .finished(false)
                .actions(new ArrayList<>())
                .observations(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .needPdf(hasPdfRequirement(userInput))
                .build();
    }

    // 找到 hasPdfRequirement 方法并修改
    private static boolean hasPdfRequirement(String input) {
        if (input == null) {
            return false;
        }
        String text = input.toLowerCase();
        // 把触发关键词改为文件、导出、md、html 等
        return text.contains("文件") || text.contains("导出") || text.contains("生成文档")
                || text.contains("markdown") || text.contains("html");
    }

    public void nextRound() {
        this.round++;
    }

    public void addAction(AgentAction action) {
        this.actions.add(action);
    }

    public void addObservation(AgentObservation observation) {
        this.observations.add(observation);
    }

    public AgentObservation getLastObservation() {
        if (observations == null || observations.isEmpty()) {
            return null;
        }
        return observations.get(observations.size() - 1);
    }
}