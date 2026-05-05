package com.nexusjobs.portal.repository;

import com.nexusjobs.portal.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);
    long countByUserIdAndIsReadFalse(Long userId);
}
