package com.example.recruitment.controller;

import com.example.recruitment.entity.Application;
import com.example.recruitment.repository.ApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screening")
public class ScreeningController {

    private final ApplicationRepository applicationRepository;

    public ScreeningController(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getRankedApplicationsForJob(@PathVariable Long jobId) {

        List<Application> applications = applicationRepository.findByJobIdOrderByScreeningScoreDesc(jobId);

//        if (applications.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }

        // Note: For production, use a DTO to return only necessary, non-sensitive data
        return ResponseEntity.ok(applications);
    }
}