package com.nexusjobs.portal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "seeker_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", nullable = false)
    private User seeker;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.APPLIED;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String steps = "APPLIED";  // Comma-separated history

    @Column(updatable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }

    @Transient
    public String getStatusLabel() {
        return switch (status) {
            case APPLIED -> "Applied";
            case SCREENING -> "Screening";
            case INTERVIEW -> "Interview";
            case OFFERED -> "Offered 🎉";
            case REJECTED -> "Rejected";
        };
    }

    @Transient
    public String getStatusBadgeClass() {
        return switch (status) {
            case APPLIED -> "badge-muted";
            case SCREENING -> "badge-info";
            case INTERVIEW -> "badge-warning";
            case OFFERED -> "badge-accent";
            case REJECTED -> "badge-danger";
        };
    }

    public enum Status {
        APPLIED, SCREENING, INTERVIEW, OFFERED, REJECTED
    }
}
