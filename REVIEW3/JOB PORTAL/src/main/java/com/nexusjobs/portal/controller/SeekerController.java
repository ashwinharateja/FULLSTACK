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
@RequestMapping("/dashboard/seeker")
@RequiredArgsConstructor
public class SeekerController {

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
        // Invalidate the stale session
        try { request.getSession(false).invalidate(); } catch (Exception ignored) {}
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        return "redirect:/login?expired=true";
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails principal,
                            @RequestParam(required = false) String q,
                            Model model) {
        User user = getCurrentUser(principal);
        List<Application> myApps = applicationService.getBySeeker(user.getId());
        List<Long> appliedIds = myApps.stream().map(a -> a.getJob().getId()).toList();
        List<Job> jobs = (q != null && !q.isBlank()) ? jobService.search(q) : jobService.getAllActive();

        model.addAttribute("user", user);
        model.addAttribute("jobs", jobs);
        model.addAttribute("myApplications", myApps);
        model.addAttribute("appliedJobIds", appliedIds);
        model.addAttribute("recommendations", jobService.getRecommendations(user, appliedIds));
        model.addAttribute("notifications", notificationService.getForUser(user.getId()));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user.getId()));
        model.addAttribute("searchQuery", q != null ? q : "");
        model.addAttribute("totalApplied", myApps.size());
        model.addAttribute("interviews", applicationService.countBySeekerAndStatus(user.getId(), Application.Status.INTERVIEW));
        model.addAttribute("offers", applicationService.countBySeekerAndStatus(user.getId(), Application.Status.OFFERED));
        return "seeker-dashboard";
    }

    @PostMapping("/apply/{jobId}")
    public String apply(@AuthenticationPrincipal UserDetails principal,
                        @PathVariable Long jobId,
                        @RequestParam(required = false) String coverLetter,
                        RedirectAttributes ra) {
        User user = getCurrentUser(principal);
        Job job = jobService.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        try {
            applicationService.apply(job, user, coverLetter);
            jobService.incrementApplicationCount(jobId);

            // ── Notify the employer about the new application ──
            notificationService.add(
                    job.getEmployer(),
                    "info",
                    "📥 New Application",
                    user.getName() + " applied to \"" + job.getTitle() + "\""
            );

            ra.addFlashAttribute("successMsg", "Applied to " + job.getTitle() + " successfully!");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/dashboard/seeker";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String skills,
                                @RequestParam(required = false) String bio,
                                @RequestParam(required = false) Integer experience,
                                RedirectAttributes ra) {
        User user = getCurrentUser(principal);
        User updates = User.builder().name(name).title(title).location(location)
                .skills(skills).bio(bio).experience(experience).build();
        userService.updateProfile(user.getId(), updates);
        ra.addFlashAttribute("successMsg", "Profile updated!");
        return "redirect:/dashboard/seeker";
    }

    @PostMapping("/notifications/read")
    public String markRead(@AuthenticationPrincipal UserDetails principal) {
        notificationService.markAllRead(getCurrentUser(principal).getId());
        return "redirect:/dashboard/seeker";
    }
}
