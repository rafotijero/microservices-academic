package com.proyecto.audit.repository;

import com.proyecto.audit.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Optional<AuditEvent> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

    List<AuditEvent> findByStudentIdOrderByTimestampDesc(Long studentId);

    List<AuditEvent> findByCourseIdOrderByTimestampDesc(Long courseId);

    Page<AuditEvent> findByEventType(String eventType, Pageable pageable);

    @Query("SELECT a FROM AuditEvent a WHERE " +
           "(:eventType IS NULL OR a.eventType = :eventType) AND " +
           "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR a.timestamp <= :endDate) " +
           "ORDER BY a.timestamp DESC")
    Page<AuditEvent> findByFilters(
            @Param("eventType") String eventType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    Long countByEventType(String eventType);

    List<AuditEvent> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT a.courseCode, a.courseName, COUNT(a) as cnt FROM AuditEvent a " +
           "WHERE a.eventType = 'ENROLLMENT_CREATED' " +
           "GROUP BY a.courseCode, a.courseName " +
           "ORDER BY cnt DESC")
    List<Object[]> findTopCoursesByEnrollments(Pageable pageable);
}
