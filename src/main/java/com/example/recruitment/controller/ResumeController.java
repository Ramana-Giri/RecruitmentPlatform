package com.example.recruitment.controller;

import com.example.recruitment.entity.Application;
import com.example.recruitment.repository.ApplicationRepository;
import com.example.recruitment.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin/resumes")
public class ResumeController {

    private final ApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;

    public ResumeController(ApplicationRepository applicationRepository, FileStorageService fileStorageService) {
        this.applicationRepository = applicationRepository;
        this.fileStorageService = fileStorageService;
    }

    // This method maps to: /api/admin/resumes/application/{applicationId}
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long applicationId, HttpServletRequest request) {

        // 1. Fetch the Application and get the file path
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found for ID: " + applicationId));

        String filePath = application.getResumeFilePath();
        if (filePath == null || filePath.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 2. Load the file as a Resource
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        String contentType = null;

        try {
            // Determine content type dynamically (e.g., application/pdf)
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default to generic binary if unable to determine
            contentType = "application/octet-stream";
        }

        String fileName = resource.getFilename();
        if (fileName == null) {
            fileName = "resume_download";
        }

        // 3. Return the file content
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"") // Forces browser to download
                .body(resource);
    }
}
