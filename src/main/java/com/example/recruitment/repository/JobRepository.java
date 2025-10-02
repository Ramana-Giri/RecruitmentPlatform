package com.example.recruitment.repository;

import com.example.recruitment.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByIsActiveTrue();

    // NEW: Custom query to search across multiple fields for active jobs
    @Query("SELECT j FROM Job j WHERE j.isActive = TRUE AND " +
            "(LOWER(j.title) LIKE :query OR " +
            "LOWER(j.description) LIKE :query OR " +
            "LOWER(j.requiredSkills) LIKE :query)")
    List<Job> searchActiveJobs(@Param("query") String query);
}