package com.example.recruitment.service;

import com.example.recruitment.entity.Candidate;
import com.example.recruitment.repository.CandidateRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public Candidate registerCandidate(Candidate candidate) {
        // No encryption needed. Store raw password directly.
        candidate.setPassword(candidate.getRawPassword());
        candidate.setRawPassword(null);

        return candidateRepository.save(candidate);
    }

    public Optional<Candidate> findCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    public Optional<Candidate> findCandidateByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }

    public boolean checkPassword(String email, String rawPassword) {
        Optional<Candidate> candidateOpt = findCandidateByEmail(email);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();
            // Compare raw password with stored plain text password
            return rawPassword.equals(candidate.getPassword());
        }
        return false;
    }
}