# Microservicio de Autenticación - Sistema Académico

Microservicio de autenticación desarrollado con Spring Boot 3.x que proporciona gestión de usuarios y autenticación basada en JWT para un sistema académico.

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring Security 6
- JWT (JSON Web Tokens)
- JPA/Hibernate
- PostgreSQL 15
- Maven
- Lombok
- Docker & Docker Compose

## Características

- Registro de usuarios (estudiantes, profesores, administradores)
- Autenticación con JWT (Access Token y Refresh Token)
- Gestión de roles (ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
- Validación de tokens para otros microservicios
- Cifrado de contraseñas con BCrypt
- Manejo global de excepciones
- Configuración de CORS
- Arquitectura en capas (Controller, Service, Repository)

## Endpoints Disponibles

### Públicos (sin autenticación requerida)

#### 1. Registro de Usuario
```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "password": "password123",
  "roles": ["ROLE_STUDENT"]
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente"
}
```

#### 2. Login
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "juan.perez",
  "password": "password123"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "roles": ["ROLE_STUDENT"]
}
```

#### 3. Renovar Token
```http
POST http://localhost:8081/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Protegidos (requieren autenticación)

#### 4. Obtener Usuario Actual
```http
GET http://localhost:8081/api/auth/me
Authorization: Bearer {accessToken}
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "enabled": true,
  "roles": ["ROLE_STUDENT"],
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

#### 5. Cerrar Sesión
```http
POST http://localhost:8081/api/auth/logout
Authorization: Bearer {accessToken}
```

#### 6. Validar Token (Para otros microservicios)
```http
GET http://localhost:8081/api/auth/validate?token={accessToken}
Authorization: Bearer {accessToken}
```

**Respuesta exitosa (200 OK):**
```json
{
  "valid": true,
  "userId": 1,
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "roles": ["ROLE_STUDENT"]
}
```

## Configuración

### Variables de Entorno

El microservicio utiliza las siguientes variables de entorno:

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| SPRING_PROFILE | Perfil de Spring (dev/prod) | dev |
| DB_HOST | Host de PostgreSQL | localhost |
| DB_PORT | Puerto de PostgreSQL | 5432 |
| DB_NAME | Nombre de la base de datos | auth_db |
| DB_USER | Usuario de la base de datos | postgres |
| DB_PASSWORD | Contraseña de la base de datos | postgres |
| JWT_SECRET | Secreto para firmar JWT | (clave predeterminada) |
| JWT_ACCESS_EXPIRATION | Expiración del access token (ms) | 3600000 (1 hora) |
| JWT_REFRESH_EXPIRATION | Expiración del refresh token (ms) | 86400000 (24 horas) |
| PORT | Puerto del servidor | 8081 |

## Ejecución con Docker

### Prerrequisitos

- Docker Desktop instalado y ejecutándose
- Puertos 8081 y 5432 disponibles

### Instrucciones

#### 1. Construir las imágenes
```bash
docker-compose build
```

#### 2. Levantar los servicios
```bash
docker-compose up -d
```

Este comando iniciará:
- Contenedor PostgreSQL (puerto 5432)
- Contenedor del microservicio de autenticación (puerto 8081)

#### 3. Verificar el estado de los contenedores
```bash
docker-compose ps
```

#### 4. Ver los logs
```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs solo del microservicio
docker-compose logs -f auth-service

# Ver logs solo de PostgreSQL
docker-compose logs -f postgres
```

#### 5. Detener los servicios
```bash
docker-compose down
```

#### 6. Detener y eliminar volúmenes (borra los datos)
```bash
docker-compose down -v
```

### URL de Acceso

Una vez que los servicios estén ejecutándose:

- **API del Microservicio:** http://localhost:8081
- **Base de datos PostgreSQL:** localhost:5432

## Ejemplos de Uso con cURL

### Registrar un estudiante
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "maria.garcia",
    "email": "maria.garcia@example.com",
    "password": "password123",
    "roles": ["ROLE_STUDENT"]
  }'
```

### Hacer login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "maria.garcia",
    "password": "password123"
  }'
```

### Obtener información del usuario actual
```bash
curl -X GET http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer {TU_ACCESS_TOKEN}"
```

## Estructura del Proyecto

```
ms-academic-auth/
├── src/main/java/com/proyecto/auth/
│   ├── config/              # Configuraciones (Security, CORS)
│   ├── controller/          # Controladores REST
│   ├── dto/                 # Data Transfer Objects
│   ├── entity/              # Entidades JPA
│   ├── exception/           # Manejo de excepciones
│   ├── repository/          # Repositorios JPA
│   ├── security/            # Seguridad y JWT
│   ├── service/             # Lógica de negocio
│   └── AuthApplication.java # Clase principal
├── src/main/resources/
│   ├── application.yml      # Configuración principal
│   ├── application-dev.yml  # Configuración desarrollo
│   ├── application-prod.yml # Configuración producción
│   └── init.sql            # Script de inicialización
├── Dockerfile              # Configuración de Docker
├── docker-compose.yml      # Orquestación de contenedores
├── .dockerignore          # Archivos ignorados por Docker
├── pom.xml                # Dependencias Maven
└── README.md              # Este archivo
```

## Roles Disponibles

El sistema incluye tres roles predefinidos:

1. **ROLE_STUDENT** - Estudiantes
2. **ROLE_TEACHER** - Profesores
3. **ROLE_ADMIN** - Administradores

Estos roles se crean automáticamente al iniciar la base de datos mediante el script `init.sql`.

## Validaciones

### Registro de Usuario
- Username: 3-50 caracteres, obligatorio
- Email: formato válido, obligatorio
- Password: mínimo 6 caracteres, obligatorio
- Roles: opcional (por defecto ROLE_STUDENT)

### Login
- UsernameOrEmail: obligatorio
- Password: obligatorio

## Manejo de Errores

El microservicio incluye un manejador global de excepciones que retorna respuestas estructuradas:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación",
  "path": "/api/auth/register",
  "details": [
    "El email debe ser válido",
    "La contraseña debe tener al menos 6 caracteres"
  ]
}
```

## Seguridad

- Contraseñas cifradas con BCrypt
- Tokens JWT firmados con HMAC-SHA256
- Sesiones stateless (no se guardan en servidor)
- CORS configurado
- Validación de entrada en todos los endpoints
- Usuario no root en contenedor Docker

## Solución de Problemas

### El contenedor no inicia
1. Verificar que Docker Desktop esté ejecutándose
2. Verificar que los puertos 8081 y 5432 estén disponibles
3. Revisar los logs: `docker-compose logs`

### Error de conexión a la base de datos
1. Esperar a que PostgreSQL esté completamente iniciado (health check)
2. Verificar las credenciales en docker-compose.yml
3. Revisar logs de PostgreSQL: `docker-compose logs postgres`

### El token expira muy rápido
Ajustar las variables de entorno `JWT_ACCESS_EXPIRATION` y `JWT_REFRESH_EXPIRATION` en docker-compose.yml

## Desarrollo Local (sin Docker)

Si deseas ejecutar el proyecto localmente sin Docker:

1. Instalar PostgreSQL y crear la base de datos `auth_db`
2. Ejecutar el script `init.sql`
3. Configurar las variables de entorno o usar application-dev.yml
4. Ejecutar: `mvn spring-boot:run`

## Licencia

Este proyecto es parte de un sistema académico de ejemplo.

## Contacto

Para preguntas o sugerencias, contactar al equipo de desarrollo.
