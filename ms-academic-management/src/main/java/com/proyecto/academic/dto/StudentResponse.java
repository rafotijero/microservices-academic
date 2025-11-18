package com.proyecto.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String carrera;
}
