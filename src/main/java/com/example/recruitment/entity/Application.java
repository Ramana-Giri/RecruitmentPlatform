package com.example.recruitment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private LocalDateTime appliedDate = LocalDateTime.now();
    private String status = "SUBMITTED";
    private Double screeningScore;

    public Application() {}

    // Getters
    public Long getId() { return id; }
    public Candidate getCandidate() { return candidate; }
    public Job getJob() { return job; }
    public LocalDateTime getAppliedDate() { return appliedDate; }
    public String getStatus() { return status; }
    public Double getScreeningScore() { return screeningScore; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
    public void setJob(Job job) { this.job = job; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }
    public void setStatus(String status) { this.status = status; }
    public void setScreeningScore(Double screeningScore) { this.screeningScore = screeningScore; }
}