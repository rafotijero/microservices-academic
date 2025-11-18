package com.proyecto.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentMessageDTO {
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer courseCredits;
    private LocalDateTime enrollmentDate;
    private String status;
}
