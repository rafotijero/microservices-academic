package com.proyecto.academic.repository;

import com.proyecto.academic.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByCodigo(String codigo);
    boolean existsByEmail(String email);
    boolean existsByCodigo(String codigo);
}
