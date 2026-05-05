package com.smartcampus.events.service;

import com.smartcampus.events.dto.AttendanceRequest;
import com.smartcampus.events.dto.EventResponse;
import com.smartcampus.events.dto.RegistrationInfoResponse;
import com.smartcampus.events.dto.RegistrationRequest;
import com.smartcampus.events.exception.ApiException;
import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.Registration;
import com.smartcampus.events.model.RegistrationStatus;
import com.smartcampus.events.model.User;
import com.smartcampus.events.repository.EventRepository;
import com.smartcampus.events.repository.RegistrationRepository;
import com.smartcampus.events.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RegistrationService {
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public RegistrationService(RegistrationRepository registrationRepository, UserRepository userRepository, EventRepository eventRepository, EventService eventService) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    public EventResponse register(RegistrationRequest request) {
        validateEventId(request);
        User user = resolveUser(request.getUserId(), request.getEmail());
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new ApiException("Event not found"));
        validateDepartmentAccess(user, event);
        Registration existing = registrationRepository.findByUserAndEvent(user, event).orElse(null);
        if (existing != null && existing.getStatus() == RegistrationStatus.REGISTERED) {
            throw new ApiException("Already registered for this event");
        }
        long activeCount = registrationRepository.countByEventAndStatus(event, RegistrationStatus.REGISTERED);
        Registration registration = existing != null ? existing : new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(activeCount >= event.getSeats() ? RegistrationStatus.WAITLISTED : RegistrationStatus.REGISTERED);
        registration.setAttendanceMarked(false);
        registration.setAttendanceToken(UUID.randomUUID().toString());
        registrationRepository.save(registration);
        log.info("User {} ({}) registered for event {} ({}) with status {}", user.getId(), user.getEmail(), event.getId(), event.getTitle(), registration.getStatus());
        return eventService.getEventById(event.getId(), user.getId());
    }

    public EventResponse cancel(RegistrationRequest request) {
        validateEventId(request);
        User user = resolveUser(request.getUserId(), request.getEmail());
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new ApiException("Event not found"));
        Registration registration = registrationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new ApiException("Registration not found"));
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        log.info("User {} ({}) cancelled registration for event {} ({})", user.getId(), user.getEmail(), event.getId(), event.getTitle());
        promoteWaitlisted(event);
        return eventService.getEventById(event.getId(), user.getId());
    }

    public List<EventResponse> myEvents(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        log.info("Fetching my-events for user {} ({})", user.getId(), user.getEmail());
        return registrationRepository.findByUserAndStatus(user, RegistrationStatus.REGISTERED)
                .stream()
                .map(reg -> eventService.getEventById(reg.getEvent().getId(), userId))
                .toList();
    }

    public List<EventResponse> myEventsByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException("User not found"));
        log.info("Fetching my-events for user {} ({})", user.getId(), user.getEmail());
        return registrationRepository.findByUserAndStatus(user, RegistrationStatus.REGISTERED)
                .stream()
                .map(reg -> eventService.getEventById(reg.getEvent().getId(), user.getId()))
                .toList();
    }

    public List<Map<String, Object>> myEventsDashboard(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        return registrationRepository.findByUser(user).stream()
                .map(reg -> Map.<String, Object>of(
                        "eventId", reg.getEvent().getId(),
                        "title", reg.getEvent().getTitle(),
                        "date", reg.getEvent().getDate(),
                        "department", reg.getEvent().getDepartment(),
                        "registrationStatus", reg.getStatus().name(),
                        "attendanceMarked", reg.isAttendanceMarked()
                )).toList();
    }

    public List<RegistrationInfoResponse> getRegistrationsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException("Event not found"));
        return registrationRepository.findByEvent(event)
                .stream()
                .map(reg -> {
                    RegistrationInfoResponse info = new RegistrationInfoResponse();
                    info.setRegistrationId(reg.getId());
                    info.setUserId(reg.getUser().getId());
                    info.setUserName(reg.getUser().getName());
                    info.setUserEmail(reg.getUser().getEmail());
                    info.setStatus(reg.getStatus().name());
                    info.setAttendanceMarked(reg.isAttendanceMarked());
                    info.setAttendanceToken(reg.getAttendanceToken());
                    return info;
                }).toList();
    }

    public EventResponse waitlist(RegistrationRequest request) {
        validateEventId(request);
        User user = resolveUser(request.getUserId(), request.getEmail());
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new ApiException("Event not found"));
        validateDepartmentAccess(user, event);
        Registration registration = registrationRepository.findByUserAndEvent(user, event).orElseGet(Registration::new);
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(RegistrationStatus.WAITLISTED);
        registration.setAttendanceMarked(false);
        registration.setAttendanceToken(UUID.randomUUID().toString());
        registrationRepository.save(registration);
        return eventService.getEventById(event.getId(), user.getId());
    }

    public Map<String, Object> markAttendance(AttendanceRequest request) {
        Registration registration = registrationRepository.findByAttendanceToken(request.getToken())
                .orElseThrow(() -> new ApiException("Invalid attendance token"));
        registration.setAttendanceMarked(true);
        registration.setStatus(RegistrationStatus.ATTENDED);
        registrationRepository.save(registration);
        return Map.of("message", "Attendance marked");
    }

    public Map<String, Object> participantDecision(Long eventId, Long registrationId, boolean approve) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException("Event not found"));
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ApiException("Registration not found"));
        if (!registration.getEvent().getId().equals(eventId)) {
            throw new ApiException("Registration does not belong to event");
        }
        if (!approve) {
            registration.setStatus(RegistrationStatus.REJECTED);
            registrationRepository.save(registration);
            return Map.of("message", "Participant rejected");
        }
        long activeCount = registrationRepository.countByEventAndStatus(event, RegistrationStatus.REGISTERED);
        registration.setStatus(activeCount >= event.getSeats() ? RegistrationStatus.WAITLISTED : RegistrationStatus.REGISTERED);
        registrationRepository.save(registration);
        return Map.of("message", "Participant approved");
    }

    private void promoteWaitlisted(Event event) {
        long activeCount = registrationRepository.countByEventAndStatus(event, RegistrationStatus.REGISTERED);
        if (activeCount >= event.getSeats()) return;
        List<Registration> waitlisted = registrationRepository.findByEventAndStatus(event, RegistrationStatus.WAITLISTED)
                .stream()
                .sorted(Comparator.comparing(Registration::getId))
                .toList();
        if (!waitlisted.isEmpty()) {
            Registration next = waitlisted.get(0);
            next.setStatus(RegistrationStatus.REGISTERED);
            registrationRepository.save(next);
        }
    }

    private void validateEventId(RegistrationRequest request) {
        if (request.getEventId() == null) {
            throw new ApiException("Event id is required");
        }
    }

    private User resolveUser(Long userId, String email) {
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmail(email).orElseThrow(() -> new ApiException("User not found"));
        }
        if (userId != null) {
            return userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        }
        throw new ApiException("Either userId or email is required");
    }

    private void validateDepartmentAccess(User user, Event event) {
        if (user.getDepartment() != null && !user.getDepartment().isBlank() &&
                event.getDepartment() != null &&
                !event.getDepartment().equalsIgnoreCase(user.getDepartment())) {
            throw new ApiException("You can only register for your department events");
        }
    }
}
