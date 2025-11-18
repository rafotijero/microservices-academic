package com.proyecto.academic.repository;

import com.proyecto.academic.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
