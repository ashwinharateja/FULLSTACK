package com.nexusjobs.portal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String companyLogo;

    @Column(nullable = false)
    private String location;

    private String type;         // Full-time, Contract, Part-time
    private boolean remote;

    private Long salaryMin;
    private Long salaryMax;

    @Column(columnDefinition = "TEXT")
    private String skills;       // Comma-separated

    @Column(columnDefinition = "TEXT")
    private String description;

    private String experience;
    private String category;

    @Builder.Default
    private String status = "active";   // active, paused

    @Builder.Default
    private int views = 0;

    @Builder.Default
    private int applicationCount = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Transient
    public List<String> getSkillsList() {
        if (skills == null || skills.isBlank()) return List.of();
        return List.of(skills.split(","));
    }

    @Transient
    public String getFormattedSalary() {
        if (salaryMin == null || salaryMax == null) return "—";
        return "$" + formatK(salaryMin) + " – $" + formatK(salaryMax);
    }

    private String formatK(long val) {
        return val >= 1000 ? (val / 1000) + "k" : String.valueOf(val);
    }
}
