# Microservicio de Gestión Académica (Matrículas)

Microservicio desarrollado con Spring Boot 3.x para la gestión de estudiantes, cursos y matrículas académicas.

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA / Hibernate
- PostgreSQL 15
- Maven
- Lombok
- Docker & Docker Compose
- RestTemplate para integración con microservicio de autenticación

## Características

- Gestión completa de estudiantes (CRUD)
- Gestión completa de cursos (CRUD)
- Sistema de matrículas con validación de cupos
- Integración con microservicio de autenticación mediante JWT
- Validaciones de negocio
- Manejo global de excepciones
- Configuración de CORS
- Datos de ejemplo precargados
- Arquitectura en capas

## Prerequisitos

1. Docker Desktop instalado
2. El microservicio `ms-academic-auth` debe estar ejecutándose
3. La red Docker `academic-network` debe estar creada

### Crear la red Docker compartida

```bash
docker network create academic-network
```

## Estructura del Proyecto

```
ms-academic-management/
├── src/main/java/com/proyecto/academic/
│   ├── config/              # Configuraciones (CORS, Security, RestTemplate)
│   ├── controller/          # Controladores REST
│   ├── dto/                 # DTOs para Request/Response
│   ├── entity/              # Entidades JPA
│   ├── repository/          # Repositorios JPA
│   ├── service/             # Lógica de negocio
│   ├── security/            # Filtro de autenticación JWT
│   └── exception/           # Excepciones personalizadas y manejador global
├── src/main/resources/
│   ├── application.yml      # Configuración principal
│   ├── application-dev.yml  # Configuración desarrollo
│   └── data.sql             # Datos de ejemplo
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── pom.xml
└── README.md
```

## Instalación y Ejecución

### Paso 1: Asegurar que el microservicio de autenticación esté ejecutándose

```bash
cd ../ms-academic-auth
docker-compose up -d
```

### Paso 2: Construir la imagen del microservicio

```bash
docker-compose build
```

### Paso 3: Levantar los servicios

```bash
docker-compose up -d
```

### Paso 4: Verificar que los servicios estén ejecutándose

```bash
docker-compose ps
```

## Detener los Servicios

```bash
docker-compose down
```

Para eliminar también los volúmenes de datos:

```bash
docker-compose down -v
```

## Acceso al Microservicio

- **URL Base**: http://localhost:8082
- **Base de Datos PostgreSQL**: localhost:5433
  - Usuario: postgres
  - Contraseña: postgres
  - Base de datos: academic_db

## Endpoints Disponibles

### Estudiantes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/students` | Crear un nuevo estudiante |
| GET | `/api/students` | Listar todos los estudiantes |
| GET | `/api/students/{id}` | Obtener estudiante por ID |
| PUT | `/api/students/{id}` | Actualizar estudiante |
| DELETE | `/api/students/{id}` | Eliminar estudiante |

### Cursos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/courses` | Crear un nuevo curso |
| GET | `/api/courses` | Listar todos los cursos |
| GET | `/api/courses/{id}` | Obtener curso por ID |
| PUT | `/api/courses/{id}` | Actualizar curso |
| DELETE | `/api/courses/{id}` | Eliminar curso |

### Matrículas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/enrollments` | Matricular estudiante en un curso |
| DELETE | `/api/enrollments/{id}` | Desmatricular estudiante |
| GET | `/api/enrollments/student/{studentId}` | Obtener matrículas de un estudiante |
| GET | `/api/enrollments/course/{courseId}` | Obtener estudiantes de un curso |

## Ejemplos de Uso con cURL

### 1. Obtener un token JWT (desde el microservicio de autenticación)

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Guarda el token recibido para usarlo en las siguientes peticiones.

### 2. Listar todos los estudiantes

```bash
curl -X GET http://localhost:8082/api/students \
  -H "Authorization: Bearer TU_TOKEN_JWT"
```

### 3. Crear un nuevo estudiante

```bash
curl -X POST http://localhost:8082/api/students \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "E006",
    "nombre": "Pedro",
    "apellido": "López",
    "email": "pedro.lopez@universidad.edu",
    "telefono": "987654326",
    "carrera": "Ingeniería de Sistemas"
  }'
```

### 4. Listar todos los cursos

```bash
curl -X GET http://localhost:8082/api/courses \
  -H "Authorization: Bearer TU_TOKEN_JWT"
```

### 5. Matricular un estudiante en un curso

```bash
curl -X POST http://localhost:8082/api/enrollments \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "estudianteId": 1,
    "cursoId": 1
  }'
```

### 6. Obtener matrículas de un estudiante

```bash
curl -X GET http://localhost:8082/api/enrollments/student/1 \
  -H "Authorization: Bearer TU_TOKEN_JWT"
```

### 7. Obtener estudiantes matriculados en un curso

```bash
curl -X GET http://localhost:8082/api/enrollments/course/1 \
  -H "Authorization: Bearer TU_TOKEN_JWT"
```

## Datos de Ejemplo Precargados

Al iniciar el microservicio, se cargan automáticamente:

### Estudiantes (5)
- E001 - Juan Pérez
- E002 - María González
- E003 - Carlos Rodríguez
- E004 - Ana Martínez
- E005 - Luis Sánchez

### Cursos (5)
- CS101 - Introducción a la Programación (4 créditos, 27/30 cupos)
- CS201 - Estructura de Datos (5 créditos, 23/25 cupos)
- CS301 - Algoritmos Avanzados (5 créditos, 20/20 cupos)
- CS401 - Bases de Datos (4 créditos, 30/30 cupos)
- CS501 - Arquitectura de Software (5 créditos, 15/15 cupos)

### Matrículas (3)
- Juan Pérez en Introducción a la Programación
- María González en Introducción a la Programación
- Carlos Rodríguez en Estructura de Datos

## Validaciones Implementadas

- No se permite matricular si no hay cupos disponibles
- No se permite matricular dos veces al mismo estudiante en el mismo curso
- Los cupos disponibles se reducen automáticamente al matricular
- Los cupos disponibles se incrementan automáticamente al desmatricular
- Se valida que el estudiante y curso existan antes de matricular
- Validación de campos obligatorios en todas las entidades
- Validación de emails
- Códigos únicos para estudiantes y cursos

## Integración con Microservicio de Autenticación

El microservicio valida todos los requests mediante un filtro JWT que:
1. Extrae el token del header `Authorization`
2. Llama al endpoint `/api/auth/validate` del microservicio de autenticación
3. Permite o rechaza la petición según la respuesta

**Configuración de la URL del servicio de autenticación:**
- Desarrollo: `http://localhost:8081`
- Docker: `http://ms-auth:8081`

## Conectar Ambos Microservicios en la Misma Red Docker

Si ambos microservicios están ejecutándose en contenedores Docker:

```bash
# Asegurar que ambos microservicios usen la misma red
docker network create academic-network

# Ejecutar el microservicio de autenticación en la red
cd ../ms-academic-auth
docker-compose up -d

# Ejecutar este microservicio en la misma red
cd ../ms-academic-management
docker-compose up -d
```

## Logs y Debugging

Ver logs en tiempo real:

```bash
docker-compose logs -f ms-academic-management
```

Ver logs de la base de datos:

```bash
docker-compose logs -f postgres-academic
```

## Solución de Problemas

### El microservicio no puede conectarse a la base de datos

Verifica que PostgreSQL esté ejecutándose:
```bash
docker-compose ps postgres-academic
```

### Error al validar el token JWT

Asegúrate de que:
1. El microservicio de autenticación esté ejecutándose
2. Ambos microservicios estén en la misma red Docker
3. El token JWT sea válido y no haya expirado

### Los datos de ejemplo no se cargan

Verifica los logs para ver errores en la ejecución de `data.sql`:
```bash
docker-compose logs ms-academic-management | grep -i error
```

## Arquitectura

El microservicio sigue una arquitectura en capas:

- **Controller**: Manejo de peticiones HTTP
- **Service**: Lógica de negocio
- **Repository**: Acceso a datos
- **Entity**: Modelo de dominio
- **DTO**: Objetos de transferencia de datos
- **Exception**: Manejo de errores

## Licencia

Este es un proyecto DEMO para fines educativos.
