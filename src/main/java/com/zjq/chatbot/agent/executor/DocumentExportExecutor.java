package com.zjq.chatbot.agent.executor;

import cn.hutool.core.io.FileUtil;
import com.zjq.chatbot.agent.model.AgentAction;
import com.zjq.chatbot.agent.model.AgentContext;
import com.zjq.chatbot.agent.model.AgentObservation;
import com.zjq.chatbot.constant.FileConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DocumentExportExecutor implements DirectToolExecutor {

    @Override
    public String supportToolName() {
        return "documentExportTool"; // 新工具名称
    }

    @Override
    public AgentObservation execute(AgentAction action, AgentContext context) {
        String content = action.getToolInput();

        if (!StringUtils.hasText(content)) {
            return AgentObservation.fail("tool", "正文为空，无法生成文件");
        }

        // 定义文件保存目录
        String fileDir = FileConstant.FILE_SAVE_DIR + "/file";
        FileUtil.mkdir(fileDir);

        String timestamp = String.valueOf(System.currentTimeMillis());

        // 1. 直接生成 Markdown 文件
        String mdFileName = "Guide_" + timestamp + ".md";
        String mdPath = fileDir + "/" + mdFileName;
        FileUtil.writeUtf8String(content, mdPath);

        // 2. 生成 HTML 文件 (用 pre 标签或基础 HTML 骨架包裹 Markdown 文本)
        String htmlFileName = "Guide_" + timestamp + ".html";
        String htmlPath = fileDir + "/" + htmlFileName;
        String htmlContent = "<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>生成文档</title>\n" +
                "    <style>body { font-family: sans-serif; line-height: 1.6; padding: 20px; max-width: 800px; margin: auto; }</style>\n" +
                "</head>\n<body>\n" +
                "\n<pre style='white-space: pre-wrap; word-wrap: break-word;'>\n" +
                content + "\n</pre>\n</body>\n</html>";
        FileUtil.writeUtf8String(htmlContent, htmlPath);

        String resultMsg = "Markdown 已保存至: " + mdPath + "\nHTML 已保存至: " + htmlPath;
        return AgentObservation.success("tool", resultMsg, "文件生成成功");
    }
}