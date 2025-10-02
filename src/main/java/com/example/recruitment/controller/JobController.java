package com.example.recruitment.controller;

import com.example.recruitment.entity.Job;
import com.example.recruitment.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Public endpoint for candidates
    @GetMapping("/active")
    public List<Job> getAllActiveJobs() {
        return jobService.getAllActiveJobs();
    }

    @GetMapping("/search")
    public List<Job> searchJobs(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return jobService.getAllActiveJobs();
        }
        return jobService.searchActiveJobs(keyword);
    }

    // Admin/HR endpoints below (secured by SecurityConfig.java)

    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping // For Job posting
    public Job createJob(@RequestBody Job job) {
        return jobService.postNewJob(job);
    }

    @PutMapping("/{id}") // For Job update
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job updatedJob) {
        return jobService.getJobById(id)
                .map(existingJob -> {
                    existingJob.setTitle(updatedJob.getTitle());
                    existingJob.setDescription(updatedJob.getDescription());
                    existingJob.setRequiredSkills(updatedJob.getRequiredSkills());
                    existingJob.setLocation(updatedJob.getLocation());
                    existingJob.setActive(updatedJob.isActive());
                    return ResponseEntity.ok(jobService.postNewJob(existingJob));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // For Job archive
    public ResponseEntity<Void> archiveJob(@PathVariable Long id) {
        try {
            jobService.archiveJob(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}