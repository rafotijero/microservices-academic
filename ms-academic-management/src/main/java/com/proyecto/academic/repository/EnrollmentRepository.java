package com.proyecto.academic.repository;

import com.proyecto.academic.entity.Enrollment;
import com.proyecto.academic.entity.Enrollment.EstadoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByEstudianteId(Long studentId);
    List<Enrollment> findByCursoId(Long courseId);
    Optional<Enrollment> findByEstudianteIdAndCursoIdAndEstado(Long studentId, Long courseId, EstadoMatricula estado);
    boolean existsByEstudianteIdAndCursoIdAndEstado(Long studentId, Long courseId, EstadoMatricula estado);
}
