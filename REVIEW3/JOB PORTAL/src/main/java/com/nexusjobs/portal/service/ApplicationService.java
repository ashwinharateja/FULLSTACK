package com.nexusjobs.portal.service;

import com.nexusjobs.portal.model.Application;
import com.nexusjobs.portal.model.Job;
import com.nexusjobs.portal.model.User;
import com.nexusjobs.portal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    @Transactional
    public Application apply(Job job, User seeker, String coverLetter) {
        if (applicationRepository.existsByJobIdAndSeekerId(job.getId(), seeker.getId())) {
            throw new IllegalStateException("Already applied to this job.");
        }
        Application app = Application.builder()
                .job(job)
                .seeker(seeker)
                .coverLetter(coverLetter)
                .status(Application.Status.APPLIED)
                .steps("APPLIED")
                .build();
        return applicationRepository.save(app);
    }

    public List<Application> getBySeeker(Long seekerId) {
        return applicationRepository.findBySeekerId(seekerId);
    }

    public List<Application> getByEmployer(Long employerId) {
        return applicationRepository.findByEmployerId(employerId);
    }

    public List<Application> getByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getAll() {
        return applicationRepository.findAllOrderByAppliedAtDesc();
    }

    public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }

    /**
     * Updates an application status AND fires a real-time notification to the seeker.
     */
    @Transactional
    public Application updateStatus(Long appId, Application.Status newStatus) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        Application.Status oldStatus = app.getStatus();
        app.setStatus(newStatus);

        // Append to steps history if not already present
        String steps = app.getSteps();
        if (!steps.contains(newStatus.name())) {
            app.setSteps(steps + "," + newStatus.name());
        }

        Application saved = applicationRepository.save(app);

        // ── Fire notification to seeker whenever status actually changes ──────
        if (oldStatus != newStatus) {
            User seeker     = app.getSeeker();
            String jobTitle = app.getJob().getTitle();
            String company  = app.getJob().getCompany();

            notificationService.add(
                    seeker,
                    resolveType(newStatus),
                    resolveTitle(newStatus),
                    resolveMessage(newStatus, jobTitle, company)
            );
        }

        return saved;
    }

    @Transactional
    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    public boolean hasApplied(Long jobId, Long seekerId) {
        return applicationRepository.existsByJobIdAndSeekerId(jobId, seekerId);
    }

    public long countBySeeker(Long seekerId)  { return applicationRepository.countBySeekerId(seekerId); }
    public long countAll()                    { return applicationRepository.count(); }

    public long countBySeekerAndStatus(Long seekerId, Application.Status status) {
        return applicationRepository.countBySeekerIdAndStatus(seekerId, status);
    }

    // ── Notification content helpers ──────────────────────────────────────────

    private String resolveType(Application.Status status) {
        return switch (status) {
            case OFFERED   -> "success";
            case REJECTED  -> "danger";
            case INTERVIEW -> "info";
            case SCREENING -> "info";
            default        -> "info";
        };
    }

    private String resolveTitle(Application.Status status) {
        return switch (status) {
            case SCREENING -> "🔍 Application Under Review";
            case INTERVIEW -> "🎉 Interview Invitation!";
            case OFFERED   -> "🏆 Job Offer Received!";
            case REJECTED  -> "📋 Application Update";
            default        -> "📌 Application Status Changed";
        };
    }

    private String resolveMessage(Application.Status status, String jobTitle, String company) {
        return switch (status) {
            case SCREENING -> company + " is reviewing your application for \"" + jobTitle + "\".";
            case INTERVIEW -> "Congratulations! " + company + " wants to interview you for \"" + jobTitle + "\". Check your email for details.";
            case OFFERED   -> "🎊 Amazing news! " + company + " has extended a job offer for \"" + jobTitle + "\". Respond soon!";
            case REJECTED  -> company + " has reviewed your application for \"" + jobTitle + "\" and decided to move forward with other candidates.";
            default        -> "Your application for \"" + jobTitle + "\" at " + company + " has been updated to: " + status.name();
        };
    }
}
