-- Insertar estudiantes de ejemplo
INSERT INTO students (codigo, nombre, apellido, email, telefono, carrera)
VALUES
    ('E001', 'Juan', 'Pérez', 'juan.perez@universidad.edu', '987654321', 'Ingeniería de Sistemas'),
    ('E002', 'María', 'González', 'maria.gonzalez@universidad.edu', '987654322', 'Ingeniería de Software'),
    ('E003', 'Carlos', 'Rodríguez', 'carlos.rodriguez@universidad.edu', '987654323', 'Ciencias de la Computación'),
    ('E004', 'Ana', 'Martínez', 'ana.martinez@universidad.edu', '987654324', 'Ingeniería de Sistemas'),
    ('E005', 'Luis', 'Sánchez', 'luis.sanchez@universidad.edu', '987654325', 'Ingeniería de Software')
ON CONFLICT (codigo) DO NOTHING;

-- Insertar cursos de ejemplo
INSERT INTO courses (codigo, nombre, creditos, cupos_totales, cupos_disponibles)
VALUES
    ('CS101', 'Introducción a la Programación', 4, 30, 27),
    ('CS201', 'Estructura de Datos', 5, 25, 23),
    ('CS301', 'Algoritmos Avanzados', 5, 20, 20),
    ('CS401', 'Bases de Datos', 4, 30, 30),
    ('CS501', 'Arquitectura de Software', 5, 15, 15)
ON CONFLICT (codigo) DO NOTHING;

-- Insertar matrículas de ejemplo
INSERT INTO enrollments (estudiante_id, curso_id, fecha_matricula, estado)
SELECT s.id, c.id, CURRENT_TIMESTAMP, 'ACTIVA'
FROM students s, courses c
WHERE s.codigo = 'E001' AND c.codigo = 'CS101'
AND NOT EXISTS (
    SELECT 1 FROM enrollments e
    WHERE e.estudiante_id = s.id AND e.curso_id = c.id
);

INSERT INTO enrollments (estudiante_id, curso_id, fecha_matricula, estado)
SELECT s.id, c.id, CURRENT_TIMESTAMP, 'ACTIVA'
FROM students s, courses c
WHERE s.codigo = 'E002' AND c.codigo = 'CS101'
AND NOT EXISTS (
    SELECT 1 FROM enrollments e
    WHERE e.estudiante_id = s.id AND e.curso_id = c.id
);

INSERT INTO enrollments (estudiante_id, curso_id, fecha_matricula, estado)
SELECT s.id, c.id, CURRENT_TIMESTAMP, 'ACTIVA'
FROM students s, courses c
WHERE s.codigo = 'E003' AND c.codigo = 'CS201'
AND NOT EXISTS (
    SELECT 1 FROM enrollments e
    WHERE e.estudiante_id = s.id AND e.curso_id = c.id
);
