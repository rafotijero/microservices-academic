package com.proyecto.audit.controller;

import com.proyecto.audit.dto.AuditEventResponseDTO;
import com.proyecto.audit.dto.AuditStatsDTO;
import com.proyecto.audit.dto.StudentHistoryDTO;
import com.proyecto.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/events")
    public ResponseEntity<Page<AuditEventResponseDTO>> getAllEvents(Pageable pageable) {
        return ResponseEntity.ok(auditService.getAllEvents(pageable));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<AuditEventResponseDTO> getEventById(@PathVariable Long id) {
        return auditService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/events/student/{studentId}")
    public ResponseEntity<StudentHistoryDTO> getStudentHistory(@PathVariable Long studentId) {
        return ResponseEntity.ok(auditService.getStudentHistory(studentId));
    }

    @GetMapping("/events/course/{courseId}")
    public ResponseEntity<List<AuditEventResponseDTO>> getCourseHistory(@PathVariable Long courseId) {
        return ResponseEntity.ok(auditService.getCourseHistory(courseId));
    }

    @GetMapping("/events/search")
    public ResponseEntity<Page<AuditEventResponseDTO>> searchEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.searchEvents(eventType, startDate, endDate, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<AuditStatsDTO> getStats() {
        return ResponseEntity.ok(auditService.getStats());
    }
}
