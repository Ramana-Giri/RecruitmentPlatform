package com.example.recruitment.repository;

import com.example.recruitment.entity.ResumeData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeDataRepository extends JpaRepository<ResumeData, Long> {
    // Custom finder if needed, e.g., findByApplicationId
    Optional<ResumeData> findByApplicationId(Long applicationId);
}