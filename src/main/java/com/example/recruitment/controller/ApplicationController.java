//package com.example.recruitment.controller;
//
//import com.example.recruitment.entity.Candidate;
//import com.example.recruitment.repository.ApplicationRepository;
//import com.example.recruitment.repository.CandidateRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin")
//// This controller is protected by the ROLE_ADMIN check in SecurityConfig
//public class AdminController {
//
//    private final CandidateRepository candidateRepository;
//    private final ApplicationRepository applicationRepository;
//
//    public AdminController(CandidateRepository candidateRepository, ApplicationRepository applicationRepository) {
//        this.candidateRepository = candidateRepository;
//        this.applicationRepository = applicationRepository;
//    }
//
//    @GetMapping("/candidates")
//    public List<Candidate> getAllCandidates() {
//        // Censor passwords before returning
//        List<Candidate> candidates = candidateRepository.findAll();
//        candidates.forEach(c -> c.setPassword(null));
//        return candidates;
//    }
//
//    @GetMapping("/applications")
//    public List<com.example.recruitment.entity.Application> getAllApplications() {
//        return applicationRepository.findAll();
//    }
//
//    @PutMapping("/application/{id}/status")
//    public ResponseEntity<com.example.recruitment.entity.Application> updateApplicationStatus(
//            @PathVariable Long id,
//            @RequestParam String status) {
//
//        return applicationRepository.findById(id)
//                .map(app -> {
//                    app.setStatus(status);
//                    return ResponseEntity.ok(applicationRepository.save(app));
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }
//}

package com.example.recruitment.controller;

import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.Candidate;
import com.example.recruitment.entity.Job;
import com.example.recruitment.repository.ApplicationRepository;
import com.example.recruitment.repository.CandidateRepository;
import com.example.recruitment.repository.JobRepository;
import com.example.recruitment.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;         // <--- NEW
    private final CandidateRepository candidateRepository; // <--- NEW
    private final FileStorageService fileStorageService;   // <--- NEW (A temporary/conceptual file handler)

    public ApplicationController(ApplicationRepository applicationRepository, JobRepository jobRepository, CandidateRepository candidateRepository, FileStorageService fileStorageService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.fileStorageService = fileStorageService;
    }

    // In ApplicationController.java
    @GetMapping("/candidate/{candidateId}")
    public List<Application> getApplicationsByCandidate(@PathVariable Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    // EXISTING: All Applications (Recruiter view - moved from AdminController)
    @GetMapping("/all")
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // EXISTING: Update Status (Recruiter action - moved from AdminController)
    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return applicationRepository.findById(id)
                .map(app -> {
                    app.setStatus(status);
                    return ResponseEntity.ok(applicationRepository.save(app));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/submit")
    public ResponseEntity<Void> submitApplication(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("jobId") Long jobId,
            @RequestParam("file") MultipartFile file, // Handles the resume upload
            @RequestParam(value = "notes", required = false) String notes) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // 1. Fetch related entities
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new Exception("Job not found"));

            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new Exception("Candidate not found"));

            // 2. Save the resume file to the file system/storage
            // This method needs to be implemented in FileStorageService
            String filePath = fileStorageService.saveResume(file, candidateId, jobId);

            // 3. Create the Application entity
            Application newApp = new Application();
            newApp.setJob(job);
            newApp.setCandidate(candidate);
            newApp.setStatus("SUBMITTED"); // Initial status
            newApp.setAppliedDate(LocalDate.now().atStartOfDay());
            newApp.setResumeFilePath(filePath); // Store the file path

            // 4. Save to the database
            applicationRepository.save(newApp);

            // Return 201 CREATED status upon successful submission
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            // Log the exception and return a 500 status code
            System.err.println("Application submission failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}