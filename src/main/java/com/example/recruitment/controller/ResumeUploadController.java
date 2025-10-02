package com.example.recruitment.controller;

import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.Candidate;
import com.example.recruitment.entity.Job;
import com.example.recruitment.entity.ResumeData;
import com.example.recruitment.repository.ApplicationRepository;
import com.example.recruitment.repository.ResumeDataRepository;
import com.example.recruitment.service.CandidateService;
import com.example.recruitment.service.JobService;
import com.example.recruitment.service.ResumeParserService;
import com.example.recruitment.service.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/apply")
public class ResumeUploadController {

    private final CandidateService candidateService;
    private final JobService jobService;
    private final ResumeParserService parserService;
    private final ScreeningService screeningService;
    private final ApplicationRepository applicationRepository;
    private final ResumeDataRepository resumeDataRepository;

    public ResumeUploadController(CandidateService candidateService, JobService jobService, ResumeParserService parserService, ScreeningService screeningService, ApplicationRepository applicationRepository, ResumeDataRepository resumeDataRepository) {
        this.candidateService = candidateService;
        this.jobService = jobService;
        this.parserService = parserService;
        this.screeningService = screeningService;
        this.applicationRepository = applicationRepository;
        this.resumeDataRepository = resumeDataRepository;
    }

    @PostMapping("/{jobId}/candidate/{candidateId}")
    public ResponseEntity<?> uploadResumeAndApply(
            @PathVariable Long jobId,
            @PathVariable Long candidateId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File must not be empty.");
        }

        Candidate candidate = candidateService.findCandidateById(candidateId).orElse(null);
        Job job = jobService.getJobById(jobId).orElse(null);

        if (candidate == null || job == null) {
            return ResponseEntity.notFound().build();
        }

        if (applicationRepository.existsByCandidateIdAndJobId(candidateId, jobId)) {
            return ResponseEntity.badRequest().body("Candidate has already applied for this job.");
        }

        try {
            // 1. Create Application
            Application application = new Application();
            application.setCandidate(candidate);
            application.setJob(job);
            application = applicationRepository.save(application);

            // 2. Parse Resume
            ResumeData resumeData = parserService.parseResume(file);
            resumeData.setApplication(application);
            resumeData = resumeDataRepository.save(resumeData);

            // 3. Screen Resume
            screeningService.calculateScreeningScore(application, resumeData, job);
            applicationRepository.save(application); // Save score/status update

            String scoreDisplay = String.format("%.0f%%", application.getScreeningScore() * 100);

            return ResponseEntity.ok(
                    "Application successful. Initial Score: " + scoreDisplay
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Resume processing failed: " + e.getMessage());
        }
    }
}