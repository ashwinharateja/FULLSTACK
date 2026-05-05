package com.nexusjobs.portal.repository;

import com.nexusjobs.portal.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findBySeekerId(Long seekerId);
    List<Application> findByJobId(Long jobId);
    boolean existsByJobIdAndSeekerId(Long jobId, Long seekerId);

    @Query("SELECT a FROM Application a WHERE a.job.employer.id = :employerId ORDER BY a.appliedAt DESC")
    List<Application> findByEmployerId(@Param("employerId") Long employerId);

    @Query("SELECT a FROM Application a ORDER BY a.appliedAt DESC")
    List<Application> findAllOrderByAppliedAtDesc();

    long countBySeekerId(Long seekerId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.seeker.id = :seekerId AND a.status = :status")
    long countBySeekerIdAndStatus(@Param("seekerId") Long seekerId,
                                   @Param("status") Application.Status status);
}
