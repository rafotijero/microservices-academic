package com.proyecto.academic.service;

import com.proyecto.academic.dto.CourseResponse;
import com.proyecto.academic.dto.EnrollmentMessageDTO;
import com.proyecto.academic.dto.EnrollmentRequest;
import com.proyecto.academic.dto.EnrollmentResponse;
import com.proyecto.academic.dto.StudentResponse;
import com.proyecto.academic.entity.Course;
import com.proyecto.academic.entity.Enrollment;
import com.proyecto.academic.entity.Enrollment.EstadoMatricula;
import com.proyecto.academic.entity.Student;
import com.proyecto.academic.exception.BusinessException;
import com.proyecto.academic.exception.ResourceNotFoundException;
import com.proyecto.academic.repository.CourseRepository;
import com.proyecto.academic.repository.EnrollmentRepository;
import com.proyecto.academic.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RabbitMQProducerService rabbitMQProducerService;

    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con ID: " + request.getEstudianteId()));

        Course course = courseRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso no encontrado con ID: " + request.getCursoId()));

        if (enrollmentRepository.existsByEstudianteIdAndCursoIdAndEstado(
                request.getEstudianteId(), request.getCursoId(), EstadoMatricula.ACTIVA)) {
            throw new BusinessException("El estudiante ya está matriculado en este curso");
        }

        if (course.getCuposDisponibles() <= 0) {
            throw new BusinessException("No hay cupos disponibles para este curso");
        }

        course.setCuposDisponibles(course.getCuposDisponibles() - 1);
        courseRepository.save(course);

        Enrollment enrollment = new Enrollment();
        enrollment.setEstudiante(student);
        enrollment.setCurso(course);
        enrollment.setFechaMatricula(LocalDateTime.now());
        enrollment.setEstado(EstadoMatricula.ACTIVA);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        log.info("Matrícula creada exitosamente: ID={}, Student={} {}, Course={}",
                savedEnrollment.getId(),
                student.getNombre(),
                student.getApellido(),
                course.getCodigo());

        // Publicar mensaje en RabbitMQ (no debe bloquear si falla)
        try {
            EnrollmentMessageDTO message = EnrollmentMessageDTO.builder()
                    .enrollmentId(savedEnrollment.getId())
                    .studentId(student.getId())
                    .studentName(student.getNombre() + " " + student.getApellido())
                    .studentEmail(student.getEmail())
                    .courseId(course.getId())
                    .courseCode(course.getCodigo())
                    .courseName(course.getNombre())
                    .courseCredits(course.getCreditos())
                    .enrollmentDate(savedEnrollment.getFechaMatricula())
                    .status(savedEnrollment.getEstado().name())
                    .build();

            rabbitMQProducerService.publishEnrollmentCreated(message);
        } catch (Exception e) {
            log.error("Error al publicar mensaje en RabbitMQ para matrícula ID={}: {}",
                    savedEnrollment.getId(), e.getMessage());
        }

        return mapToResponse(savedEnrollment);
    }

    @Transactional
    public void unenrollStudent(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Matrícula no encontrada con ID: " + enrollmentId));

        if (enrollment.getEstado() == EstadoMatricula.CANCELADA) {
            throw new BusinessException("La matrícula ya está cancelada");
        }

        Course course = enrollment.getCurso();
        course.setCuposDisponibles(course.getCuposDisponibles() + 1);
        courseRepository.save(course);

        enrollment.setEstado(EstadoMatricula.CANCELADA);
        enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + studentId);
        }
        return enrollmentRepository.findByEstudianteId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Curso no encontrado con ID: " + courseId);
        }
        return enrollmentRepository.findByCursoId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        Student student = enrollment.getEstudiante();
        Course course = enrollment.getCurso();

        StudentResponse studentResponse = new StudentResponse(
                student.getId(),
                student.getCodigo(),
                student.getNombre(),
                student.getApellido(),
                student.getEmail(),
                student.getTelefono(),
                student.getCarrera()
        );

        CourseResponse courseResponse = new CourseResponse(
                course.getId(),
                course.getCodigo(),
                course.getNombre(),
                course.getCreditos(),
                course.getCuposTotales(),
                course.getCuposDisponibles()
        );

        return new EnrollmentResponse(
                enrollment.getId(),
                studentResponse,
                courseResponse,
                enrollment.getFechaMatricula(),
                enrollment.getEstado().name()
        );
    }
}
