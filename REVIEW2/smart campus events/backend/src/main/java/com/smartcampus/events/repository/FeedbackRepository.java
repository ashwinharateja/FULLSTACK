package com.smartcampus.events.repository;

import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.Feedback;
import com.smartcampus.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEvent(Event event);
    List<Feedback> findByUser(User user);
}
