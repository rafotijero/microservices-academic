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
public class StudentHistoryDTO {
    private Long studentId;
    private String studentName;
    private List<EventSummaryDTO> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventSummaryDTO {
        private Long id;
        private String eventType;
        private String courseCode;
        private String courseName;
        private String timestamp;
    }
}
