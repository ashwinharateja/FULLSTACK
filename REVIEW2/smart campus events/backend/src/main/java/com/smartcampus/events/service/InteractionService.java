package com.smartcampus.events.service;

import com.smartcampus.events.dto.EventResponse;
import com.smartcampus.events.dto.FeedbackRequest;
import com.smartcampus.events.exception.ApiException;
import com.smartcampus.events.model.Bookmark;
import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.Feedback;
import com.smartcampus.events.model.User;
import com.smartcampus.events.repository.BookmarkRepository;
import com.smartcampus.events.repository.EventRepository;
import com.smartcampus.events.repository.FeedbackRepository;
import com.smartcampus.events.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InteractionService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;
    private final EventService eventService;

    public InteractionService(BookmarkRepository bookmarkRepository, UserRepository userRepository, EventRepository eventRepository, FeedbackRepository feedbackRepository, EventService eventService) {
        this.bookmarkRepository = bookmarkRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
        this.eventService = eventService;
    }

    public Map<String, String> toggleBookmark(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException("Event not found"));
        var existing = bookmarkRepository.findByUserAndEvent(user, event);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return Map.of("message", "Bookmark removed");
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setEvent(event);
        bookmarkRepository.save(bookmark);
        return Map.of("message", "Bookmarked");
    }

    public List<EventResponse> bookmarkedEvents(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        return bookmarkRepository.findByUser(user)
                .stream()
                .map(bookmark -> eventService.getEventById(bookmark.getEvent().getId(), userId))
                .toList();
    }

    public Map<String, String> addFeedback(FeedbackRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ApiException("User not found"));
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new ApiException("Event not found"));
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setEvent(event);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedbackRepository.save(feedback);
        return Map.of("message", "Feedback submitted");
    }

    public List<Map<String, Object>> feedbackByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException("Event not found"));
        return feedbackRepository.findByEvent(event).stream()
                .map(item -> Map.<String, Object>of(
                        "id", item.getId(),
                        "user", item.getUser().getName(),
                        "rating", item.getRating(),
                        "comment", item.getComment()
                )).toList();
    }
}
