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
import com.example.recruitment.repository.ApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;

    public ApplicationController(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
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


}