package com.example.recruitment.repository;

import com.example.recruitment.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
    List<Application> findByJobIdOrderByScreeningScoreDesc(Long jobId);
    List<Application> findByCandidateId(Long candidateId);
}