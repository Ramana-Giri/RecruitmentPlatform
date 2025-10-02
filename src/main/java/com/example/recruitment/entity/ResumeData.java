package com.example.recruitment.entity;

import jakarta.persistence.*;

@Entity
public class ResumeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;

    private String originalFileName;
    private String parsedName;
    private String parsedEmail;
    private String parsedPhone;
    @Lob
    private String parsedSkills;
    @Lob
    private String rawText;

    public ResumeData() {}

    // Getters
    public Long getId() { return id; }
    public Application getApplication() { return application; }
    public String getOriginalFileName() { return originalFileName; }
    public String getParsedName() { return parsedName; }
    public String getParsedEmail() { return parsedEmail; }
    public String getParsedPhone() { return parsedPhone; }
    public String getParsedSkills() { return parsedSkills; }
    public String getRawText() { return rawText; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setApplication(Application application) { this.application = application; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public void setParsedName(String parsedName) { this.parsedName = parsedName; }
    public void setParsedEmail(String parsedEmail) { this.parsedEmail = parsedEmail; }
    public void setParsedPhone(String parsedPhone) { this.parsedPhone = parsedPhone; }
    public void setParsedSkills(String parsedSkills) { this.parsedSkills = parsedSkills; }
    public void setRawText(String rawText) { this.rawText = rawText; }
}