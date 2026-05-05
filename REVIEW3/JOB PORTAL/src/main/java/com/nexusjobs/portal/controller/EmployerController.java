package com.nexusjobs.portal.controller;

import com.nexusjobs.portal.model.*;
import com.nexusjobs.portal.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;

    private User getCurrentUser(UserDetails p) {
        return userService.findByEmail(p.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found: " + p.getUsername()));
    }

    /** Handles stale sessions (H2 restart wipes DB but cookie still exists) */
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalStateException.class)
    public String handleStaleSession(IllegalStateException ex,
                                     jakarta.servlet.http.HttpServletRequest request,
                                     jakarta.servlet.http.HttpServletResponse response) {
        try { request.getSession(false).invalidate(); } catch (Exception ignored) {}
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        return "redirect:/login?expired=true";
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = getCurrentUser(principal);
        List<Job> myJobs = jobService.getByEmployer(user);
        List<Application> myApps = applicationService.getByEmployer(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("myJobs", myJobs);
        model.addAttribute("myApplications", myApps);
        model.addAttribute("notifications", notificationService.getForUser(user.getId()));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user.getId()));

        // Stats
        model.addAttribute("activeJobsCount", myJobs.stream().filter(j -> "active".equals(j.getStatus())).count());
        model.addAttribute("totalApplications", myApps.size());
        model.addAttribute("shortlisted", myApps.stream()
                .filter(a -> a.getStatus() == Application.Status.INTERVIEW
                          || a.getStatus() == Application.Status.OFFERED).count());
        model.addAttribute("totalViews", myJobs.stream().mapToInt(Job::getViews).sum());
        return "employer-dashboard";
    }

    @PostMapping("/jobs/create")
    public String createJob(@AuthenticationPrincipal UserDetails principal,
                            @RequestParam String title,
                            @RequestParam String location,
                            @RequestParam String type,
                            @RequestParam(defaultValue = "false") boolean remote,
                            @RequestParam(required = false) Long salaryMin,
                            @RequestParam(required = false) Long salaryMax,
                            @RequestParam(required = false) String skills,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) String experience,
                            @RequestParam(required = false) String category,
                            RedirectAttributes ra) {
        User user = getCurrentUser(principal);
        String co = user.getCompany() != null ? user.getCompany() : user.getName();
        jobService.createJob(user, title, co, "🏢", location, type, remote,
                salaryMin, salaryMax, skills, description, experience, category);
        ra.addFlashAttribute("successMsg", "Job posted successfully!");
        return "redirect:/dashboard/employer";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJobStatus(@AuthenticationPrincipal UserDetails principal,
                                   @PathVariable Long id, RedirectAttributes ra) {
        Job job = jobService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        String newStatus = "active".equals(job.getStatus()) ? "paused" : "active";
        jobService.updateStatus(id, newStatus);
        ra.addFlashAttribute("successMsg", "Job status updated to " + newStatus);
        return "redirect:/dashboard/employer";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@AuthenticationPrincipal UserDetails principal,
                            @PathVariable Long id, RedirectAttributes ra) {
        jobService.deleteJob(id);
        ra.addFlashAttribute("successMsg", "Job deleted.");
        return "redirect:/dashboard/employer";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
                                           @RequestParam String status,
                                           RedirectAttributes ra) {
        Application.Status newStatus = Application.Status.valueOf(status.toUpperCase());
        applicationService.updateStatus(id, newStatus);
        ra.addFlashAttribute("successMsg", "Application status updated to " + status);
        return "redirect:/dashboard/employer";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String company,
                                @RequestParam(required = false) String industry,
                                @RequestParam(required = false) String website,
                                @RequestParam(required = false) String about,
                                RedirectAttributes ra) {
        User user = getCurrentUser(principal);
        User updates = User.builder().name(name).company(company)
                .industry(industry).website(website).about(about).build();
        userService.updateProfile(user.getId(), updates);
        ra.addFlashAttribute("successMsg", "Company profile updated!");
        return "redirect:/dashboard/employer";
    }
}
