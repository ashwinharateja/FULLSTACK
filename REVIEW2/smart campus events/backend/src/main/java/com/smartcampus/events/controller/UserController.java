package com.smartcampus.events.controller;

import com.smartcampus.events.dto.UserSummaryResponse;
import com.smartcampus.events.model.UserRole;
import com.smartcampus.events.repository.UserRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserSummaryResponse> getUsers() {
        return userRepository.findAll().stream().map(user -> {
            UserSummaryResponse response = new UserSummaryResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setDepartment(user.getDepartment());
            response.setRole(user.getRole().name());
            return response;
        }).toList();
    }

    @GetMapping("/students")
    public List<UserSummaryResponse> getStudents() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .map(user -> {
                    UserSummaryResponse response = new UserSummaryResponse();
                    response.setId(user.getId());
                    response.setName(user.getName());
                    response.setEmail(user.getEmail());
                    response.setDepartment(user.getDepartment());
                    response.setRole(user.getRole().name());
                    return response;
                }).toList();
    }
}
