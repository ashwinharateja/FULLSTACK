package com.nexusjobs.portal.controller;

import com.nexusjobs.portal.model.User;
import com.nexusjobs.portal.service.OtpService;
import com.nexusjobs.portal.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    // ── Standard Login / Register pages ───────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            Model model) {
        if (error   != null) model.addAttribute("error", "Invalid email or password.");
        if (logout  != null) model.addAttribute("msg",   "You have been signed out.");
        if (expired != null) model.addAttribute("error", "Your session expired. Please sign in again.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false, defaultValue = "seeker") String role,
                               Model model) {
        model.addAttribute("selectedRole", role);
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(defaultValue = "seeker") String role,
            @RequestParam(required = false) String company,
            RedirectAttributes ra,
            HttpServletRequest request) {

        try {
            User.Role userRole = role.equalsIgnoreCase("employer")
                    ? User.Role.EMPLOYER : User.Role.SEEKER;
            userService.register(firstName, lastName, email, password, userRole, company);

            // Auto-login after registration (seekers skip OTP on first registration)
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            return switch (userRole) {
                case EMPLOYER -> "redirect:/dashboard/employer";
                case SEEKER   -> "redirect:/dashboard/seeker";
                default       -> "redirect:/";
            };
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("selectedRole", role);
            return "redirect:/register";
        }
    }

    // ── Seeker OTP Login Flow ──────────────────────────────────────────────────

    /**
     * Step 1: Seeker submits email + password → verify credentials → send OTP.
     */
    @PostMapping("/seeker-login")
    public String seekerLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {

        try {
            // Authenticate credentials
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(email.trim().toLowerCase(), password);
            authenticationManager.authenticate(token);

            // Ensure the account is actually a SEEKER
            User user = userService.findByEmail(email.trim().toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (user.getRole() != User.Role.SEEKER) {
                ra.addFlashAttribute("error", "Please use the Employer / Admin login tab.");
                return "redirect:/login";
            }

            // Credentials valid → generate & send OTP
            otpService.generateAndSend(user.getEmail());
            session.setAttribute("otp_email", user.getEmail());

            return "redirect:/verify-otp";

        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            ra.addFlashAttribute("seekerError", "Invalid email or password.");
            return "redirect:/login";
        } catch (DisabledException | LockedException ex) {
            ra.addFlashAttribute("seekerError", "Account is disabled or locked.");
            return "redirect:/login";
        }
    }

    /**
     * Step 2: Show OTP entry page.
     */
    @GetMapping("/verify-otp")
    public String verifyOtpPage(HttpSession session, Model model, RedirectAttributes ra) {
        String email = (String) session.getAttribute("otp_email");
        if (email == null) {
            // No active OTP session — back to login
            return "redirect:/login";
        }
        model.addAttribute("maskedEmail", maskEmail(email));
        // Dev-mode: show OTP on page so user doesn't need to check terminal
        String devOtp = otpService.getDevOtp(email);
        if (devOtp != null) {
            model.addAttribute("devOtp", devOtp);
        }
        return "verify-otp";
    }

    /**
     * Step 3: Seeker submits the OTP → verify → grant session.
     */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes ra) {

        String email = (String) session.getAttribute("otp_email");
        if (email == null) {
            ra.addFlashAttribute("error", "Session expired. Please sign in again.");
            return "redirect:/login";
        }

        if (!otpService.validate(email, otp)) {
            ra.addFlashAttribute("otpError", "Invalid or expired OTP. Please try again.");
            return "redirect:/verify-otp";
        }

        // OTP valid — establish Spring Security session
        UsernamePasswordAuthenticationToken authToken =
                userService.findByEmail(email)
                        .map(u -> {
                            var springUser = new org.springframework.security.core.userdetails.User(
                                    u.getEmail(), u.getPassword(),
                                    java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                            "ROLE_" + u.getRole().name())));
                            return new UsernamePasswordAuthenticationToken(
                                    springUser, null, springUser.getAuthorities());
                        })
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        // Clean up OTP session marker
        session.removeAttribute("otp_email");

        log.info("Seeker {} completed OTP login successfully", email);
        return "redirect:/dashboard/seeker";
    }

    /**
     * Resend OTP (invalidates previous and sends a fresh code).
     */
    @GetMapping("/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes ra) {
        String email = (String) session.getAttribute("otp_email");
        if (email == null) {
            return "redirect:/login";
        }
        otpService.invalidate(email);
        otpService.generateAndSend(email);
        ra.addFlashAttribute("otpMsg", "A new OTP has been sent to your email.");
        return "redirect:/verify-otp";
    }

    // ── Logout ─────────────────────────────────────────────────────────────────

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/?logout";
    }

    // ── Utility ────────────────────────────────────────────────────────────────

    /** Masks email: a***@example.com */
    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return email;
        return email.charAt(0) + "***" + email.substring(at);
    }
}
