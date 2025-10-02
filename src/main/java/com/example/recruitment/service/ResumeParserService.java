package com.example.recruitment.service;

import com.example.recruitment.entity.ResumeData;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Service
public class ResumeParserService {

    public ResumeData parseResume(MultipartFile file) throws Exception {
        BodyContentHandler handler = new BodyContentHandler(10 * 1024 * 1024); // Limit to 10MB text
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();

        try (InputStream stream = file.getInputStream()) {
            parser.parse(stream, handler, metadata);
        }

        String rawText = handler.toString();

        ResumeData resumeData = new ResumeData();
        resumeData.setOriginalFileName(file.getOriginalFilename());
        resumeData.setRawText(rawText);

        // Mocked Parsing: Replace with real NLP/Regex logic in production
        resumeData.setParsedName(extractMockName(rawText));
        resumeData.setParsedEmail(extractMockEmail(rawText));
        resumeData.setParsedSkills(extractMockSkills(rawText));

        return resumeData;
    }

    private String extractMockName(String text) {
        // Very basic mock: looks for a name-like structure near the start
        String[] lines = text.split("\n");
        return lines.length > 0 ? lines[0].trim() : "Unknown Name";
    }

    private String extractMockEmail(String text) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        ).matcher(text);
        return matcher.find() ? matcher.group(0) : "email-not-found@example.com";
    }

    private String extractMockSkills(String text) {
        String lowerText = text.toLowerCase();
        StringBuilder skills = new StringBuilder();
        if (lowerText.contains("java")) skills.append("Java, ");
        if (lowerText.contains("spring")) skills.append("Spring Boot, ");
        if (lowerText.contains("sql")) skills.append("SQL, ");
        if (lowerText.contains("react")) skills.append("React, ");
        return skills.toString().replaceAll(", $", "");
    }
}