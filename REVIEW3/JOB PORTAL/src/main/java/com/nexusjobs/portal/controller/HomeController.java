package com.nexusjobs.portal.controller;

import com.nexusjobs.portal.model.User;
import com.nexusjobs.portal.service.JobService;
import com.nexusjobs.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobService jobService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        // Featured jobs for landing page (latest 6 active)
        var jobs = jobService.getAllActive();
        model.addAttribute("featuredJobs", jobs.stream().limit(6).toList());

        // Platform stats
        model.addAttribute("totalJobs",   jobService.countActive());
        model.addAttribute("totalUsers",  userService.countAll());
        model.addAttribute("seekerCount", userService.countByRole(User.Role.SEEKER));

        return "index";
    }
}
