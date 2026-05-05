package com.smartcampus.events.repository;

import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.Registration;
import com.smartcampus.events.model.RegistrationStatus;
import com.smartcampus.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByUserAndEvent(User user, Event event);
    List<Registration> findByUserAndStatus(User user, RegistrationStatus status);
    List<Registration> findByUser(User user);
    List<Registration> findByEvent(Event event);
    List<Registration> findByEventAndStatus(Event event, RegistrationStatus status);
    Optional<Registration> findByAttendanceToken(String attendanceToken);
    long countByStatus(RegistrationStatus status);
    long countByEventAndStatus(Event event, RegistrationStatus status);
}
