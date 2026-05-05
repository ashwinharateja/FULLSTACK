package com.nexusjobs.portal.repository;

import com.nexusjobs.portal.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByThreadIdOrderByTimestampAsc(String threadId);

    @Query("SELECT DISTINCT m.threadId FROM Message m WHERE m.fromUser.id = :userId OR m.toUser.id = :userId")
    List<String> findDistinctThreadIdsByUserId(@Param("userId") Long userId);
}
