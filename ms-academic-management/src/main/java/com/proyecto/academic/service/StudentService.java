package com.proyecto.academic.service;

import com.proyecto.academic.dto.StudentRequest;
import com.proyecto.academic.dto.StudentResponse;
import com.proyecto.academic.entity.Student;
import com.proyecto.academic.exception.BusinessException;
import com.proyecto.academic.exception.ResourceNotFoundException;
import com.proyecto.academic.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un estudiante con el código: " + request.getCodigo());
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un estudiante con el email: " + request.getEmail());
        }

        Student student = new Student();
        student.setCodigo(request.getCodigo());
        student.setNombre(request.getNombre());
        student.setApellido(request.getApellido());
        student.setEmail(request.getEmail());
        student.setTelefono(request.getTelefono());
        student.setCarrera(request.getCarrera());

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
        return mapToResponse(student);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        if (!student.getCodigo().equals(request.getCodigo()) &&
            studentRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un estudiante con el código: " + request.getCodigo());
        }

        if (!student.getEmail().equals(request.getEmail()) &&
            studentRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un estudiante con el email: " + request.getEmail());
        }

        student.setCodigo(request.getCodigo());
        student.setNombre(request.getNombre());
        student.setApellido(request.getApellido());
        student.setEmail(request.getEmail());
        student.setTelefono(request.getTelefono());
        student.setCarrera(request.getCarrera());

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    private StudentResponse mapToResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getCodigo(),
                student.getNombre(),
                student.getApellido(),
                student.getEmail(),
                student.getTelefono(),
                student.getCarrera()
        );
    }
}
