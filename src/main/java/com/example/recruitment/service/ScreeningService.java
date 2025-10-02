package com.example.recruitment.service;

import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.Job;
import com.example.recruitment.entity.ResumeData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ScreeningService {

    // Simple skill-match scoring
    public double calculateScreeningScore(Application application, ResumeData resumeData, Job job) {

        if (job.getRequiredSkills() == null || job.getRequiredSkills().isEmpty() || resumeData.getParsedSkills() == null) {
            application.setScreeningScore(0.0);
            application.setStatus("SCREENED_NO_MATCH");
            return 0.0;
        }

        List<String> requiredSkills = Arrays.stream(job.getRequiredSkills().toLowerCase().split("[\\s,]+"))
                .filter(s -> !s.isEmpty())
                .toList();
        String candidateSkillsText = resumeData.getParsedSkills().toLowerCase();

        long matchedSkillsCount = requiredSkills.stream()
                .filter(candidateSkillsText::contains)
                .count();

        // Score based on the proportion of required skills matched
        double score = (double) matchedSkillsCount / requiredSkills.size();

        application.setScreeningScore(Math.min(1.0, score));
        application.setStatus("SCREENED");

        return application.getScreeningScore();
    }
}