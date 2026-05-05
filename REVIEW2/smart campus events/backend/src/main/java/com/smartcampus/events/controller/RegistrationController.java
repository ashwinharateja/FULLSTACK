package com.smartcampus.events.controller;

import com.smartcampus.events.dto.AttendanceRequest;
import com.smartcampus.events.dto.EventResponse;
import com.smartcampus.events.dto.RegistrationInfoResponse;
import com.smartcampus.events.dto.RegistrationRequest;
import com.smartcampus.events.exception.ApiException;
import java.util.Map;
import com.smartcampus.events.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public EventResponse register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @DeleteMapping("/register")
    public EventResponse cancel(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long eventId,
            @RequestBody(required = false) RegistrationRequest requestBody
    ) {
        RegistrationRequest request = requestBody != null ? requestBody : new RegistrationRequest();
        if (request.getEmail() == null) request.setEmail(email);
        if (request.getEventId() == null) request.setEventId(eventId);
        return registrationService.cancel(request);
    }

    @PostMapping("/waitlist")
    public EventResponse waitlist(@RequestBody RegistrationRequest request) {
        return registrationService.waitlist(request);
    }

    @GetMapping("/my-events")
    public List<EventResponse> myEvents(@RequestParam(required = false) Long userId, @RequestParam(required = false) String email) {
        if (email != null && !email.isBlank()) {
            return registrationService.myEventsByEmail(email);
        }
        if (userId != null) {
            return registrationService.myEvents(userId);
        }
        throw new ApiException("email or userId is required");
    }

    @GetMapping("/my-events-dashboard")
    public List<Map<String, Object>> myEventsDashboard(@RequestParam Long userId) {
        return registrationService.myEventsDashboard(userId);
    }

    @GetMapping("/events/{eventId}/registrations")
    public List<RegistrationInfoResponse> registrationsByEvent(@PathVariable Long eventId) {
        return registrationService.getRegistrationsByEvent(eventId);
    }

    @GetMapping("/participants")
    public List<RegistrationInfoResponse> participants(@RequestParam Long eventId) {
        return registrationService.getRegistrationsByEvent(eventId);
    }

    @PostMapping("/attendance")
    public Map<String, Object> attendance(@Valid @RequestBody AttendanceRequest request) {
        return registrationService.markAttendance(request);
    }

    @PostMapping("/events/{eventId}/participants/decision")
    public Map<String, Object> participantDecision(@PathVariable Long eventId, @RequestParam Long registrationId, @RequestParam boolean approve) {
        return registrationService.participantDecision(eventId, registrationId, approve);
    }
}
