package com.smartcampus.events.dto;

import jakarta.validation.constraints.NotBlank;

public class AttendanceRequest {
    @NotBlank
    private String token;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
