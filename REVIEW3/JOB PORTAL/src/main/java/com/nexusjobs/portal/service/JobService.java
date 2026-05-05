package com.nexusjobs.portal.service;

import com.nexusjobs.portal.model.Job;
import com.nexusjobs.portal.model.User;
import com.nexusjobs.portal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public List<Job> getAllActive() {
        return jobRepository.findByStatusOrderByCreatedAtDesc("active");
    }

    public List<Job> search(String query) {
        if (query == null || query.isBlank()) return getAllActive();
        return jobRepository.searchActive(query.trim());
    }

    public List<Job> getFiltered(String category, String type, Boolean remote) {
        return jobRepository.findFiltered(
                (category != null && !category.equals("all")) ? category : null,
                (type != null && !type.equals("all")) ? type : null,
                remote
        );
    }

    public List<Job> getByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    public List<Job> getByEmployerId(Long id) {
        return jobRepository.findByEmployerId(id);
    }

    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    @Transactional
    public Job createJob(User employer, String title, String company, String companyLogo,
                         String location, String type, boolean remote,
                         Long salaryMin, Long salaryMax, String skills,
                         String description, String experience, String category) {
        Job job = Job.builder()
                .employer(employer)
                .title(title)
                .company(company)
                .companyLogo(companyLogo != null ? companyLogo : "🏢")
                .location(location)
                .type(type)
                .remote(remote)
                .salaryMin(salaryMin)
                .salaryMax(salaryMax)
                .skills(skills)
                .description(description)
                .experience(experience)
                .category(category)
                .status("active")
                .build();
        return jobRepository.save(job);
    }

    @Transactional
    public Job updateStatus(Long id, String status) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setStatus(status);
        return jobRepository.save(job);
    }

    @Transactional
    public void incrementViews(Long id) {
        jobRepository.findById(id).ifPresent(j -> {
            j.setViews(j.getViews() + 1);
            jobRepository.save(j);
        });
    }

    @Transactional
    public void incrementApplicationCount(Long id) {
        jobRepository.findById(id).ifPresent(j -> {
            j.setApplicationCount(j.getApplicationCount() + 1);
            jobRepository.save(j);
        });
    }

    @Transactional
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    /** Skill-match recommendations for a seeker */
    public List<Map<String, Object>> getRecommendations(User seeker, List<Long> appliedJobIds) {
        if (seeker.getSkills() == null || seeker.getSkills().isBlank()) return List.of();
        List<String> seekerSkills = seeker.getSkillsList().stream()
                .map(String::toLowerCase).collect(Collectors.toList());

        return getAllActive().stream()
                .filter(j -> !appliedJobIds.contains(j.getId()))
                .map(j -> {
                    List<String> jobSkills = j.getSkillsList().stream()
                            .map(String::toLowerCase).collect(Collectors.toList());
                    long matches = jobSkills.stream().filter(seekerSkills::contains).count();
                    int score = jobSkills.isEmpty() ? 0 :
                            (int) Math.round((matches / (double) jobSkills.size()) * 100);
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("job", j);
                    entry.put("score", score);
                    return entry;
                })
                .filter(e -> (int) e.get("score") > 0)
                .sorted((a, b) -> Integer.compare((int) b.get("score"), (int) a.get("score")))
                .limit(5)
                .collect(Collectors.toList());
    }

    public long countActive() {
        return jobRepository.findByStatus("active").size();
    }
}
