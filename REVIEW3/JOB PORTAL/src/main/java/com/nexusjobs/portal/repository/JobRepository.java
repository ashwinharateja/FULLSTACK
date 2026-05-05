package com.nexusjobs.portal.repository;

import com.nexusjobs.portal.model.Job;
import com.nexusjobs.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(String status);
    List<Job> findByEmployer(User employer);
    List<Job> findByEmployerId(Long employerId);

    @Query("SELECT j FROM Job j WHERE j.status = 'active' AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(j.skills) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Job> searchActive(@Param("q") String query);

    List<Job> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT j FROM Job j WHERE j.status = 'active' AND " +
           "(:category IS NULL OR j.category = :category) AND " +
           "(:type IS NULL OR j.type = :type) AND " +
           "(:remote IS NULL OR j.remote = :remote)")
    List<Job> findFiltered(@Param("category") String category,
                           @Param("type") String type,
                           @Param("remote") Boolean remote);
}
