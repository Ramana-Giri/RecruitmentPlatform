package com.example.recruitment.controller;

import com.example.recruitment.entity.Candidate;
import com.example.recruitment.service.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// DTOs for request/response bodies (essential for clean API)
class RegistrationRequest {
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String role;
}

class LoginRequest {
    public String email;
    public String password;
}

class LoginResponse {
    public Long id;
    public String email;
    public String role;
}

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCandidate(@RequestBody RegistrationRequest request) {
        if (candidateService.findCandidateByEmail(request.email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use.");
        }

        Candidate candidate = new Candidate();
        candidate.setFirstName(request.firstName);
        candidate.setLastName(request.lastName);
        candidate.setEmail(request.email);
        candidate.setRawPassword(request.password);
        candidate.setRole("ROLE_" + request.role);

        candidate.setPhoneNumber("N/A");
        candidate.setHeadline(request.role.equals("ADMIN") ? "Recruiter" : "Job Seeker");

        Candidate registered = candidateService.registerCandidate(candidate);

        return ResponseEntity.ok("Candidate registered successfully with ID: " + registered.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCandidate(@RequestBody LoginRequest request) {
        if (candidateService.checkPassword(request.email, request.password)) {
            Candidate candidate = candidateService.findCandidateByEmail(request.email).get();

            LoginResponse response = new LoginResponse();
            response.id = candidate.getId();
            response.email = candidate.getEmail();
            response.role = candidate.getRole().replace("ROLE_", ""); // Send clean role

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateProfile(@PathVariable Long id) {
        return candidateService.findCandidateById(id)
                .map(candidate -> {
                    // Censor sensitive data (plain text password)
                    candidate.setPassword(null); // Changed from setPasswordHash(null)
                    return ResponseEntity.ok(candidate);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}