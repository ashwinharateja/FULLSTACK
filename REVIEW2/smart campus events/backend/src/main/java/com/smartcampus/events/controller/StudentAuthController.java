package com.smartcampus.events.controller;

import com.smartcampus.events.dto.StudentLoginRequest;
import com.smartcampus.events.service.StudentAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentAuthController {
    private final StudentAuthService studentAuthService;

    public StudentAuthController(StudentAuthService studentAuthService) {
        this.studentAuthService = studentAuthService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody StudentLoginRequest request) {
        return studentAuthService.login(request);
    }
}
