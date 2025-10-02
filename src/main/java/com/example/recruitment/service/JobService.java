package com.example.recruitment.service;

import com.example.recruitment.entity.Job;
import com.example.recruitment.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> searchActiveJobs(String keyword) {
        String query = "%" + keyword.toLowerCase() + "%";
        return jobRepository.searchActiveJobs(query);
    }

    public Job postNewJob(Job job) {
        return jobRepository.save(job);
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> getAllActiveJobs() {
        return jobRepository.findByIsActiveTrue();
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job archiveJob(Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setActive(false);
                    return jobRepository.save(job);
                })
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }
}