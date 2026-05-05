package com.smartcampus.events.repository;

import com.smartcampus.events.model.Bookmark;
import com.smartcampus.events.model.Event;
import com.smartcampus.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndEvent(User user, Event event);
    List<Bookmark> findByUser(User user);
}
