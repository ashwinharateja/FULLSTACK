package com.smartcampus.events.service;

import com.smartcampus.events.dto.EventRequest;
import com.smartcampus.events.dto.EventResponse;
import com.smartcampus.events.exception.ApiException;
import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.EventStatus;
import com.smartcampus.events.model.RegistrationStatus;
import com.smartcampus.events.model.User;
import com.smartcampus.events.model.UserRole;
import com.smartcampus.events.repository.BookmarkRepository;
import com.smartcampus.events.repository.EventRepository;
import com.smartcampus.events.repository.RegistrationRepository;
import com.smartcampus.events.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository, UserRepository userRepository, BookmarkRepository bookmarkRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    public Page<EventResponse> getEvents(Long userId, String department, String type, LocalDate date, int page, int size) {
        User user = userId == null ? null : userRepository.findById(userId).orElse(null);
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (department != null && !department.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("department")), "%" + department.toLowerCase() + "%"));
            } else if (user != null && user.getRole() == UserRole.STUDENT && user.getDepartment() != null && !user.getDepartment().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("department")), user.getDepartment().toLowerCase()));
            }
            if (type != null && !type.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("type")), "%" + type.toLowerCase() + "%"));
            }
            if (date != null) {
                predicates.add(cb.equal(root.get("date"), date));
            }
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), LocalDate.now()));
            predicates.add(cb.or(
                    cb.isNull(root.get("status")),
                    cb.notEqual(root.get("status"), EventStatus.DRAFT)
            ));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Event> events = eventRepository.findAll(spec, PageRequest.of(page, size, Sort.by("date").ascending()));
        return events.map(event -> toResponse(event, user));
    }

    public EventResponse getEventById(Long id, Long userId) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException("Event not found"));
        User user = userId == null ? null : userRepository.findById(userId).orElse(null);
        return toResponse(event, user);
    }

    public EventResponse createEvent(EventRequest request) {
        Event event = new Event();
        mapRequest(event, request);
        eventRepository.save(event);
        return toResponse(event, null);
    }

    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException("Event not found"));
        mapRequest(event, request);
        eventRepository.save(event);
        return toResponse(event, null);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ApiException("Event not found"));
        eventRepository.delete(event);
    }

    private void mapRequest(Event event, EventRequest request) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setDepartment(request.getDepartment());
        event.setType(request.getType());
        event.setSeats(request.getSeats());
        event.setOrganizer(request.getOrganizer());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            event.setStatus(EventStatus.valueOf(request.getStatus().toUpperCase()));
        }
        event.setFeatured(request.isFeatured());
    }

    private EventResponse toResponse(Event event, User user) {
        long registeredCount = registrationRepository.countByEventAndStatus(event, RegistrationStatus.REGISTERED);
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setDate(event.getDate());
        response.setDepartment(event.getDepartment());
        response.setType(event.getType());
        response.setSeats(event.getSeats());
        response.setRegisteredCount(registeredCount);
        response.setSeatsAvailable(Math.max(0, event.getSeats() - registeredCount));
        response.setOrganizer(event.getOrganizer());
        response.setStatus((event.getStatus() == null ? EventStatus.PUBLISHED : event.getStatus()).name());
        response.setFeatured(event.isFeatured());
        response.setCreatedAt(event.getCreatedAt());
        if (user != null) {
            var registration = registrationRepository.findByUserAndEvent(user, event).orElse(null);
            boolean registered = registration != null && registration.getStatus() == RegistrationStatus.REGISTERED;
            boolean waitlisted = registration != null && registration.getStatus() == RegistrationStatus.WAITLISTED;
            response.setRegistered(registered);
            response.setWaitlisted(waitlisted);
            response.setBookmarked(bookmarkRepository.findByUserAndEvent(user, event).isPresent());
        }
        return response;
    }
}
