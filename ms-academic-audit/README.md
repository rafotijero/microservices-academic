# MS Academic Audit

Microservicio de auditoría para eventos de matrículas académicas. Consume mensajes de Kafka y almacena eventos para consulta histórica y estadísticas.

## Tecnologías

- Java 17
- Spring Boot 3.2
- Spring Kafka
- PostgreSQL
- Docker & Docker Compose

## Prerequisitos

- Docker Desktop instalado y ejecutándose
- Puerto 8084 disponible (microservicio)
- Puerto 5433 disponible (PostgreSQL)
- Puerto 9092/29092 disponibles (Kafka)
- Puerto 2181 disponible (Zookeeper)

## Ejecución

### Construir la imagen

```bash
docker-compose build
```

### Levantar los servicios

```bash
docker-compose up -d
```

### Detener los servicios

```bash
docker-compose down
```

### Ver logs

```bash
docker-compose logs -f ms-audit
```

### Verificar estado de los servicios

```bash
docker-compose ps
```

## URL de Acceso

- Microservicio: http://localhost:8084
- API Base: http://localhost:8084/api/audit

## Endpoints

### Listar todos los eventos (paginado)
```bash
curl http://localhost:8084/api/audit/events
curl "http://localhost:8084/api/audit/events?page=0&size=10"
```

### Buscar evento por ID
```bash
curl http://localhost:8084/api/audit/events/1
```

### Historial de un estudiante
```bash
curl http://localhost:8084/api/audit/events/student/1
```

### Historial de un curso
```bash
curl http://localhost:8084/api/audit/events/course/3
```

### Búsqueda con filtros
```bash
curl "http://localhost:8084/api/audit/events/search?eventType=ENROLLMENT_CREATED"
curl "http://localhost:8084/api/audit/events/search?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"
```

### Estadísticas
```bash
curl http://localhost:8084/api/audit/stats
```

## Verificar Kafka

### Ver topics creados
```bash
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Crear topic manualmente (si es necesario)
```bash
docker exec -it kafka kafka-topics --create --topic enrollment.events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### Producir mensaje de prueba
```bash
docker exec -it kafka kafka-console-producer --broker-list localhost:9092 --topic enrollment.events
```

Luego pegar el JSON:
```json
{"eventId":"test-001","eventType":"ENROLLMENT_CREATED","timestamp":"2024-11-17T10:30:00","enrollmentId":1,"studentId":1,"studentName":"Juan Pérez","studentEmail":"juan.perez@university.edu","courseId":3,"courseCode":"CS101","courseName":"Introducción a la Programación","courseCredits":4,"enrollmentDate":"2024-11-17T10:30:00","status":"ACTIVA"}
```

### Ver mensajes en el topic
```bash
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic enrollment.events --from-beginning
```

## Estructura del Mensaje Kafka

```json
{
  "eventId": "uuid-generado",
  "eventType": "ENROLLMENT_CREATED",
  "timestamp": "2024-11-17T10:30:00",
  "enrollmentId": 1,
  "studentId": 1,
  "studentName": "Juan Pérez",
  "studentEmail": "juan.perez@university.edu",
  "courseId": 3,
  "courseCode": "CS101",
  "courseName": "Introducción a la Programación",
  "courseCredits": 4,
  "enrollmentDate": "2024-11-17T10:30:00",
  "status": "ACTIVA"
}
```

## Tipos de Eventos

- `ENROLLMENT_CREATED`: Matrícula creada
- `ENROLLMENT_CANCELLED`: Matrícula cancelada

## Respuesta de Estadísticas

```json
{
  "totalEvents": 150,
  "enrollmentCreated": 120,
  "enrollmentCancelled": 30,
  "topCourses": [
    {
      "courseCode": "CS101",
      "courseName": "Intro to Programming",
      "enrollments": 45
    }
  ],
  "recentEvents": [...]
}
```

## Troubleshooting

### El microservicio no conecta a Kafka
- Verificar que Kafka esté healthy: `docker-compose ps`
- Ver logs de Kafka: `docker-compose logs kafka`
- Esperar a que Kafka esté completamente iniciado

### Error de conexión a PostgreSQL
- Verificar estado: `docker-compose ps audit-db`
- Ver logs: `docker-compose logs audit-db`

### Reiniciar todo desde cero
```bash
docker-compose down -v
docker-compose up -d --build
```
