package com.proyecto.academic.service;

import com.proyecto.academic.dto.CourseRequest;
import com.proyecto.academic.dto.CourseResponse;
import com.proyecto.academic.entity.Course;
import com.proyecto.academic.exception.BusinessException;
import com.proyecto.academic.exception.ResourceNotFoundException;
import com.proyecto.academic.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un curso con el código: " + request.getCodigo());
        }

        if (request.getCuposDisponibles() > request.getCuposTotales()) {
            throw new BusinessException("Los cupos disponibles no pueden ser mayores a los cupos totales");
        }

        Course course = new Course();
        course.setCodigo(request.getCodigo());
        course.setNombre(request.getNombre());
        course.setCreditos(request.getCreditos());
        course.setCuposTotales(request.getCuposTotales());
        course.setCuposDisponibles(request.getCuposDisponibles());

        Course savedCourse = courseRepository.save(course);
        return mapToResponse(savedCourse);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
        return mapToResponse(course);
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        if (!course.getCodigo().equals(request.getCodigo()) &&
            courseRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un curso con el código: " + request.getCodigo());
        }

        if (request.getCuposDisponibles() > request.getCuposTotales()) {
            throw new BusinessException("Los cupos disponibles no pueden ser mayores a los cupos totales");
        }

        course.setCodigo(request.getCodigo());
        course.setNombre(request.getNombre());
        course.setCreditos(request.getCreditos());
        course.setCuposTotales(request.getCuposTotales());
        course.setCuposDisponibles(request.getCuposDisponibles());

        Course updatedCourse = courseRepository.save(course);
        return mapToResponse(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso no encontrado con ID: " + id);
        }
        courseRepository.deleteById(id);
    }

    private CourseResponse mapToResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCodigo(),
                course.getNombre(),
                course.getCreditos(),
                course.getCuposTotales(),
                course.getCuposDisponibles()
        );
    }
}
