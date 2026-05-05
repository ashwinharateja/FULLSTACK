package com.smartcampus.events.service;

import com.smartcampus.events.model.AdminAuditLog;
import com.smartcampus.events.repository.AdminAuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final AdminAuditLogRepository adminAuditLogRepository;

    public AuditService(AdminAuditLogRepository adminAuditLogRepository) {
        this.adminAuditLogRepository = adminAuditLogRepository;
    }

    public void log(String action, String details) {
        AdminAuditLog log = new AdminAuditLog();
        log.setAction(action);
        log.setDetails(details);
        adminAuditLogRepository.save(log);
    }

    public List<AdminAuditLog> all() {
        return adminAuditLogRepository.findAll();
    }
}
