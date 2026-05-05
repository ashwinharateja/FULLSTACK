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

@Controller
@RequestMapping("/dashboard/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    private User getCurrentUser(UserDetails p) {
        return userService.findByEmail(p.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("allUsers", userService.findAll());
        model.addAttribute("allJobs", jobService.findAll());
        model.addAttribute("allApplications", applicationService.getAll());

        // Stats
        model.addAttribute("totalUsers", userService.countAll());
        model.addAttribute("seekerCount", userService.countByRole(User.Role.SEEKER));
        model.addAttribute("employerCount", userService.countByRole(User.Role.EMPLOYER));
        model.addAttribute("activeJobs", jobService.countActive());
        model.addAttribute("totalApplications", applicationService.countAll());

        long offers = applicationService.getAll().stream()
                .filter(a -> a.getStatus() == Application.Status.OFFERED).count();
        long total = applicationService.countAll();
        model.addAttribute("hireRate", total > 0 ? Math.round((offers * 100.0) / total) : 0);

        return "admin-dashboard";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails principal,
                             RedirectAttributes ra) {
        User current = getCurrentUser(principal);
        if (current.getId().equals(id)) {
            ra.addFlashAttribute("errorMsg", "You cannot delete your own account.");
            return "redirect:/dashboard/admin";
        }
        userService.deleteUser(id);
        ra.addFlashAttribute("successMsg", "User removed successfully.");
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes ra) {
        jobService.deleteJob(id);
        ra.addFlashAttribute("successMsg", "Job deleted.");
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJobStatus(@PathVariable Long id, RedirectAttributes ra) {
        Job job = jobService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        String newStatus = "active".equals(job.getStatus()) ? "paused" : "active";
        jobService.updateStatus(id, newStatus);
        ra.addFlashAttribute("successMsg", "Job status updated.");
        return "redirect:/dashboard/admin";
    }
}
