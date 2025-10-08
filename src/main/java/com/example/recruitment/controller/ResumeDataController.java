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

    // This space is reserved for methods that retrieve PARSED resume data (e.g., skills, experience),
    // which should use a different path to avoid conflicts.
    // The previous conflicting method has been removed.

    /* Example of a future, non-conflicting endpoint for parsed data:

    @GetMapping("/parsed/{applicationId}")
    public ResponseEntity<ResumeData> getParsedData(@PathVariable Long applicationId) {
        return resumeDataRepository.findById(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    */
}