package com.proyecto.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private Integer creditos;
    private Integer cuposTotales;
    private Integer cuposDisponibles;
}
