package com.smartcampus.events.service;

import com.smartcampus.events.dto.StatsResponse;
import com.smartcampus.events.exception.ApiException;
import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.EventStatus;
import com.smartcampus.events.model.RegistrationStatus;
import com.smartcampus.events.model.User;
import com.smartcampus.events.model.UserRole;
import com.smartcampus.events.repository.EventRepository;
import com.smartcampus.events.repository.RegistrationRepository;
import com.smartcampus.events.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public AdminService(UserRepository userRepository, EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException("Invalid credentials"));
        if (user.getRole() != UserRole.ADMIN || !user.getPassword().equals(password)) {
            throw new ApiException("Invalid credentials");
        }
        return Map.of("message", "Login successful", "adminId", user.getId(), "name", user.getName(), "email", user.getEmail());
    }

    public StatsResponse stats() {
        List<Event> events = eventRepository.findAll();
        String mostPopular = events.stream()
                .max(Comparator.comparingLong(event -> registrationRepository.countByEventAndStatus(event, RegistrationStatus.REGISTERED)))
                .map(Event::getTitle)
                .orElse("N/A");

        StatsResponse response = new StatsResponse();
        response.setTotalEvents(eventRepository.count());
        response.setTotalRegistrations(registrationRepository.countByStatus(RegistrationStatus.REGISTERED));
        response.setMostPopularEvent(mostPopular);
        response.setActiveUsers(userRepository.count());
        response.setWaitlistedCount(registrationRepository.countByStatus(RegistrationStatus.WAITLISTED));
        response.setPublishedEvents(events.stream().filter(event -> event.getStatus() == EventStatus.PUBLISHED).count());
        return response;
    }

    public String exportRegistrationsCsv() {
        String header = "eventId,eventTitle,registeredCount,waitlistedCount";
        String rows = eventRepository.findAll().stream()
                .map(event -> event.getId() + "," + sanitize(event.getTitle()) + ","
                        + registrationRepository.countByEventAndStatus(event, RegistrationStatus.REGISTERED) + ","
                        + registrationRepository.countByEventAndStatus(event, RegistrationStatus.WAITLISTED))
                .collect(Collectors.joining("\n"));
        return header + "\n" + rows;
    }

    public Map<String, String> notifyUsers(String scope, String message) {
        return Map.of("message", "Notification queued", "scope", scope, "content", message);
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
