package com.proyecto.academic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Student estudiante;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Course curso;

    @Column(nullable = false)
    private LocalDateTime fechaMatricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMatricula estado;

    public enum EstadoMatricula {
        ACTIVA,
        CANCELADA
    }
}
