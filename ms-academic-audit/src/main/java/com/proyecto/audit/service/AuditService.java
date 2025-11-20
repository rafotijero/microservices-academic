package com.proyecto.audit.service;

import com.proyecto.audit.dto.*;
import com.proyecto.audit.entity.AuditEvent;
import com.proyecto.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    @Transactional
    public AuditEvent saveEvent(EnrollmentEventDTO eventDTO) {
        if (auditEventRepository.existsByEventId(eventDTO.getEventId())) {
            log.warn("Event already exists with ID: {}", eventDTO.getEventId());
            return null;
        }

        AuditEvent auditEvent = AuditEvent.builder()
                .eventId(eventDTO.getEventId())
                .eventType(eventDTO.getEventType())
                .timestamp(eventDTO.getTimestamp())
                .enrollmentId(eventDTO.getEnrollmentId())
                .studentId(eventDTO.getStudentId())
                .studentName(eventDTO.getStudentName())
                .studentEmail(eventDTO.getStudentEmail())
                .courseId(eventDTO.getCourseId())
                .courseCode(eventDTO.getCourseCode())
                .courseName(eventDTO.getCourseName())
                .courseCredits(eventDTO.getCourseCredits())
                .enrollmentDate(eventDTO.getEnrollmentDate())
                .status(eventDTO.getStatus())
                .build();

        AuditEvent saved = auditEventRepository.save(auditEvent);
        log.info("Event saved successfully with ID: {}", saved.getId());
        return saved;
    }

    public Page<AuditEventResponseDTO> getAllEvents(Pageable pageable) {
        return auditEventRepository.findAll(pageable).map(this::toResponseDTO);
    }

    public Optional<AuditEventResponseDTO> getEventById(Long id) {
        return auditEventRepository.findById(id).map(this::toResponseDTO);
    }

    public StudentHistoryDTO getStudentHistory(Long studentId) {
        List<AuditEvent> events = auditEventRepository.findByStudentIdOrderByTimestampDesc(studentId);

        if (events.isEmpty()) {
            return StudentHistoryDTO.builder()
                    .studentId(studentId)
                    .studentName("Unknown")
                    .events(List.of())
                    .build();
        }

        String studentName = events.get(0).getStudentName();
        List<StudentHistoryDTO.EventSummaryDTO> eventSummaries = events.stream()
                .map(e -> StudentHistoryDTO.EventSummaryDTO.builder()
                        .id(e.getId())
                        .eventType(e.getEventType())
                        .courseCode(e.getCourseCode())
                        .courseName(e.getCourseName())
                        .timestamp(e.getTimestamp().toString())
                        .build())
                .collect(Collectors.toList());

        return StudentHistoryDTO.builder()
                .studentId(studentId)
                .studentName(studentName)
                .events(eventSummaries)
                .build();
    }

    public List<AuditEventResponseDTO> getCourseHistory(Long courseId) {
        return auditEventRepository.findByCourseIdOrderByTimestampDesc(courseId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Page<AuditEventResponseDTO> searchEvents(String eventType, LocalDateTime startDate,
                                                     LocalDateTime endDate, Pageable pageable) {
        return auditEventRepository.findByFilters(eventType, startDate, endDate, pageable)
                .map(this::toResponseDTO);
    }

    public AuditStatsDTO getStats() {
        Long totalEvents = auditEventRepository.count();
        Long enrollmentCreated = auditEventRepository.countByEventType("ENROLLMENT_CREATED");
        Long enrollmentCancelled = auditEventRepository.countByEventType("ENROLLMENT_CANCELLED");

        List<Object[]> topCoursesData = auditEventRepository.findTopCoursesByEnrollments(PageRequest.of(0, 5));
        List<AuditStatsDTO.CourseStatsDTO> topCourses = topCoursesData.stream()
                .map(row -> AuditStatsDTO.CourseStatsDTO.builder()
                        .courseCode((String) row[0])
                        .courseName((String) row[1])
                        .enrollments((Long) row[2])
                        .build())
                .collect(Collectors.toList());

        List<AuditEventResponseDTO> recentEvents = auditEventRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return AuditStatsDTO.builder()
                .totalEvents(totalEvents)
                .enrollmentCreated(enrollmentCreated)
                .enrollmentCancelled(enrollmentCancelled)
                .topCourses(topCourses)
                .recentEvents(recentEvents)
                .build();
    }

    private AuditEventResponseDTO toResponseDTO(AuditEvent event) {
        return AuditEventResponseDTO.builder()
                .id(event.getId())
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .timestamp(event.getTimestamp())
                .enrollmentId(event.getEnrollmentId())
                .studentId(event.getStudentId())
                .studentName(event.getStudentName())
                .studentEmail(event.getStudentEmail())
                .courseId(event.getCourseId())
                .courseCode(event.getCourseCode())
                .courseName(event.getCourseName())
                .courseCredits(event.getCourseCredits())
                .enrollmentDate(event.getEnrollmentDate())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
