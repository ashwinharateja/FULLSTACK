package com.smartcampus.events.config;

import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.EventStatus;
import com.smartcampus.events.model.User;
import com.smartcampus.events.model.UserRole;
import com.smartcampus.events.repository.BookmarkRepository;
import com.smartcampus.events.repository.EventRepository;
import com.smartcampus.events.repository.FeedbackRepository;
import com.smartcampus.events.repository.RegistrationRepository;
import com.smartcampus.events.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FeedbackRepository feedbackRepository;

    public DataSeeder(
            UserRepository userRepository,
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            BookmarkRepository bookmarkRepository,
            FeedbackRepository feedbackRepository
    ) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void run(String... args) {
        removeLegacyStudent("aarav@student.com");
        removeLegacyStudent("isha@student.com");
        removeLegacyStudent("student@campus.com");

        if (userRepository.findByEmail("admin@campus.com").isEmpty()) {
            User admin = new User();
            admin.setName("Campus Admin");
            admin.setEmail("admin@campus.com");
            admin.setDepartment("ADMIN");
            admin.setPassword("admin123");
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        } else {
            User admin = userRepository.findByEmail("admin@campus.com").get();
            if (admin.getPassword() == null || admin.getPassword().isBlank()) {
                admin.setPassword("admin123");
            }
            if (admin.getDepartment() == null || admin.getDepartment().isBlank()) {
                admin.setDepartment("ADMIN");
            }
            userRepository.save(admin);
        }

        seedStudent("CSE Student", "cse@student.com", "CSE", "cse123");
        seedStudent("ECE Student", "ece@student.com", "ECE", "ece123");
        seedStudent("MBA Student", "mba@student.com", "MBA", "mba123");
        seedStudent("Arts Student", "arts@student.com", "Arts", "arts123");

        if (eventRepository.count() == 0) {
            eventRepository.saveAll(List.of(
                    buildEvent("AI Hackathon", "24-hour innovation challenge with mentors.", 7, "CSE", "Hackathon", 120),
                    buildEvent("Cultural Night", "Music, dance, and open-mic by students.", 10, "Arts", "Cultural", 200),
                    buildEvent("Startup Bootcamp", "Pitching, networking, and startup strategy.", 15, "MBA", "Workshop", 80),
                    buildEvent("Robotics Expo", "Live demos of autonomous and IoT robots.", 20, "ECE", "Exhibition", 90)
            ));
        }
    }

    private void removeLegacyStudent(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            registrationRepository.deleteAll(registrationRepository.findByUser(user));
            bookmarkRepository.deleteAll(bookmarkRepository.findByUser(user));
            feedbackRepository.deleteAll(feedbackRepository.findByUser(user));
            userRepository.delete(user);
        });
    }

    private void seedStudent(String name, String email, String department, String password) {
        User student = userRepository.findByEmail(email).orElseGet(User::new);
        student.setName(name);
        student.setEmail(email);
        student.setDepartment(department);
        student.setPassword(password);
        student.setRole(UserRole.STUDENT);
        userRepository.save(student);
    }

    private Event buildEvent(String title, String description, int dayOffset, String department, String type, int seats) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setDate(LocalDate.now().plusDays(dayOffset));
        event.setDepartment(department);
        event.setType(type);
        event.setSeats(seats);
        event.setOrganizer("Campus Office");
        event.setStatus(EventStatus.PUBLISHED);
        event.setFeatured(dayOffset <= 10);
        return event;
    }
}
