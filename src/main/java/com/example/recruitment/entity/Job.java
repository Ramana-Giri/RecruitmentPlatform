package com.example.recruitment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String requiredSkills;
    private String location;
    private boolean isActive = true;
    private LocalDateTime postedDate = LocalDateTime.now();

    public Job() {}

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRequiredSkills() { return requiredSkills; }
    public String getLocation() { return location; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getPostedDate() { return postedDate; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    public void setLocation(String location) { this.location = location; }
    public void setActive(boolean active) { isActive = active; }
    public void setPostedDate(LocalDateTime postedDate) { this.postedDate = postedDate; }
}