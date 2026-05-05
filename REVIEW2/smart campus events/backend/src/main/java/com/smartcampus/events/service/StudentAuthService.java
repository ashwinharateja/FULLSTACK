package com.smartcampus.events.service;

import com.smartcampus.events.dto.StudentLoginRequest;
import com.smartcampus.events.exception.ApiException;
import com.smartcampus.events.model.User;
import com.smartcampus.events.model.UserRole;
import com.smartcampus.events.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentAuthService {
    private final UserRepository userRepository;

    public StudentAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> login(StudentLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ApiException("Invalid credentials"));
        if (user.getRole() != UserRole.STUDENT || !user.getPassword().equals(request.getPassword())) {
            throw new ApiException("Invalid credentials");
        }
        return Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "department", user.getDepartment(),
                "role", user.getRole().name()
        );
    }
}
