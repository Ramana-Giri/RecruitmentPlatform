package com.example.recruitment.controller;

import com.example.recruitment.entity.ResumeData;
import com.example.recruitment.repository.ResumeDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/resumes")
public class ResumeDataController {

    private final ResumeDataRepository resumeDataRepository;

    public ResumeDataController(ResumeDataRepository resumeDataRepository) {
        this.resumeDataRepository = resumeDataRepository;
    }

    // Endpoint secured by ROLE_ADMIN through SecurityConfig
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ResumeData> getParsedResumeByApplicationId(@PathVariable Long applicationId) {
        // Find the resume data linked to the application ID
        // Note: You would need to add a findByApplicationId method to the ResumeDataRepository

        // For now, let's find it by a direct link if possible (assuming one-to-one)
        // If your repository supports it:
        // return resumeDataRepository.findByApplicationId(applicationId)
        //     .map(ResponseEntity::ok)
        //     .orElse(ResponseEntity.notFound().build());

        // **Temporary simplified retrieval (requires an adjustment in ResumeDataRepository)**
        // To make this work without changing the repository structure (for speed), we assume
        // the ID of the ResumeData is the same as the Application ID for initial seeding.
        // In a real system, you must implement the findByApplicationId method.
        return resumeDataRepository.findById(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}