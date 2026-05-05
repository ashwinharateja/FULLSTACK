package com.smartcampus.events.controller;

import com.smartcampus.events.dto.EventRequest;
import com.smartcampus.events.dto.EventResponse;
import com.smartcampus.events.service.AuditService;
import com.smartcampus.events.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {
    private final EventService eventService;
    private final AuditService auditService;

    public EventController(EventService eventService, AuditService auditService) {
        this.eventService = eventService;
        this.auditService = auditService;
    }

    @GetMapping
    public Page<EventResponse> getEvents(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return eventService.getEvents(userId, department, type, date, page, size);
    }

    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        return eventService.getEventById(id, userId);
    }

    @PostMapping
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        auditService.log("CREATE_EVENT", "Created event " + response.getTitle());
        return response;
    }

    @PutMapping("/{id}")
    public EventResponse updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(id, request);
        auditService.log("UPDATE_EVENT", "Updated event " + response.getTitle());
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        auditService.log("DELETE_EVENT", "Deleted event id " + id);
    }
}
