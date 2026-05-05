package com.smartcampus.events.controller;

import com.smartcampus.events.dto.EventResponse;
import com.smartcampus.events.dto.FeedbackRequest;
import com.smartcampus.events.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class InteractionController {
    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/bookmark")
    public Map<String, String> toggleBookmark(@RequestParam Long userId, @RequestParam Long eventId) {
        return interactionService.toggleBookmark(userId, eventId);
    }

    @GetMapping("/bookmarks")
    public List<EventResponse> bookmarks(@RequestParam Long userId) {
        return interactionService.bookmarkedEvents(userId);
    }

    @PostMapping("/feedback")
    public Map<String, String> feedback(@Valid @RequestBody FeedbackRequest request) {
        return interactionService.addFeedback(request);
    }

    @GetMapping("/events/{eventId}/feedback")
    public List<Map<String, Object>> feedbackByEvent(@PathVariable Long eventId) {
        return interactionService.feedbackByEvent(eventId);
    }
}
