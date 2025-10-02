package com.example.recruitment.entity;

import jakarta.persistence.*;

@Entity
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @Column(name = "password")
    private String password;
    private String phoneNumber;
    private String headline;
    private String role = "ROLE_CANDIDATE"; // Simple role for security

    // Transient field for registration/login input
    @Transient
    private String rawPassword;

    public Candidate() {}

    // Getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() {return password;}
    public String getPhoneNumber() { return phoneNumber; }
    public String getHeadline() { return headline; }
    public String getRole() { return role; }
    public String getRawPassword() { return rawPassword; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setHeadline(String headline) { this.headline = headline; }
    public void setRole(String role) { this.role = role; }
    public void setRawPassword(String rawPassword) { this.rawPassword = rawPassword; }
}