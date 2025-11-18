package com.proyecto.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private StudentResponse estudiante;
    private CourseResponse curso;
    private LocalDateTime fechaMatricula;
    private String estado;
}
