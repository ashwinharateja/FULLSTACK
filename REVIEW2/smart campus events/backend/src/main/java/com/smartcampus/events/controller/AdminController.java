package com.smartcampus.events.controller;

import com.smartcampus.events.dto.AdminLoginRequest;
import com.smartcampus.events.dto.StatsResponse;
import com.smartcampus.events.model.AdminAuditLog;
import com.smartcampus.events.service.AdminService;
import com.smartcampus.events.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final AdminService adminService;
    private final AuditService auditService;

    public AdminController(AdminService adminService, AuditService auditService) {
        this.adminService = adminService;
        this.auditService = auditService;
    }

    @PostMapping("/admin/login")
    public Map<String, Object> login(@Valid @RequestBody AdminLoginRequest request) {
        return adminService.login(request.getEmail(), request.getPassword());
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return adminService.stats();
    }

    @PostMapping("/notifications")
    public Map<String, String> sendNotification(@RequestParam String scope, @RequestParam String message) {
        return adminService.notifyUsers(scope, message);
    }

    @GetMapping("/export/registrations")
    public ResponseEntity<String> exportRegistrations() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registrations.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(adminService.exportRegistrationsCsv());
    }

    @GetMapping("/audit-logs")
    public List<AdminAuditLog> auditLogs() {
        return auditService.all();
    }
}
