package com.proyecto.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditStatsDTO {
    private Long totalEvents;
    private Long enrollmentCreated;
    private Long enrollmentCancelled;
    private List<CourseStatsDTO> topCourses;
    private List<AuditEventResponseDTO> recentEvents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseStatsDTO {
        private String courseCode;
        private String courseName;
        private Long enrollments;
    }
}
