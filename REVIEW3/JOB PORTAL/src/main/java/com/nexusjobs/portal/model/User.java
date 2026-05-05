package com.nexusjobs.portal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Employer fields
    private String company;
    private String industry;
    private String website;

    @Column(columnDefinition = "TEXT")
    private String about;

    // Seeker fields
    private String title;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String skills;   // Comma-separated: "React,Node.js,TypeScript"

    private Integer experience;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Long salary;
    private String education;
    private String resume;

    // Display
    private String avatarClass;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Helper: get skills as List
    @Transient
    public List<String> getSkillsList() {
        if (skills == null || skills.isBlank()) return List.of();
        return List.of(skills.split(","));
    }

    public enum Role {
        ADMIN, EMPLOYER, SEEKER
    }
}
