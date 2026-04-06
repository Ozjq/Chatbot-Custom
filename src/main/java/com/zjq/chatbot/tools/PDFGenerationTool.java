package com.zjq.chatbot.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.zjq.chatbot.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {

        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String safeFileName = ensurePdfSuffix(fileName);
        String filePath = fileDir + "/" + safeFileName;

        try {
            FileUtil.mkdir(fileDir);

            File tempFontFile = extractFontToTempFile("static/fonts/simsun.ttf");

            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                PdfFont font = PdfFontFactory.createFont(
                        tempFontFile.getAbsolutePath(),
                        PdfEncodings.IDENTITY_H
                );

                document.setFont(font);
                document.add(new Paragraph(content));
            }

            return "PDF generated successfully to: " + filePath;
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }

    private String ensurePdfSuffix(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "output.pdf";
        }
        return fileName.toLowerCase().endsWith(".pdf") ? fileName : fileName + ".pdf";
    }

    private File extractFontToTempFile(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        File tempFile = Files.createTempFile("pdf-font-", ".ttf").toFile();
        tempFile.deleteOnExit();

        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }
}