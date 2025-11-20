# 🎓 Sistema de Microservicios Académicos

Sistema completo de gestión académica basado en microservicios con Spring Boot, PostgreSQL, RabbitMQ y notificaciones por email.

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENTE                                 │
└────────────┬─────────────────────────────┬──────────────────────┘
             │                             │
    ┌────────▼─────────┐         ┌────────▼──────────────┐
    │  ms-auth (8081)  │◄────────┤  ms-management (8082) │
    │  Autenticación   │  Valida │  Gestión Académica    │
    │  JWT             │  Token  │  CRUD Estudiantes     │
    └────────┬─────────┘         │  CRUD Cursos          │
             │                   │  Matrículas           │
    ┌────────▼─────────┐         └────────┬──────────────┘
    │  PostgreSQL      │                  │
    │  auth_db         │                  ├──────────────────┐
    │  :5434           │                  │                  │
    └──────────────────┘         ┌────────▼──────┐  ┌────────▼──────┐
                                 │  RabbitMQ     │  │    Kafka      │
    ┌──────────────────┐         │  :5672/:15672 │  │  :9092/:29092 │
    │  PostgreSQL      │         └────────┬──────┘  └────────┬──────┘
    │  academic_db     │                  │                  │
    │  :5433           │                  │ Consume          │ Consume
    └──────────────────┘         ┌────────▼──────┐  ┌────────▼──────┐
                                 │ms-notification│  │   ms-audit    │
    ┌──────────────────┐         │   (8083)      │  │   (8084)      │
    │  PostgreSQL      │         │ Envío Emails  │  │  Auditoría    │
    │  audit_db        │         └───────────────┘  └───────┬───────┘
    │  :5435           │◄───────────────────────────────────┘
    └──────────────────┘
```

## 📦 Microservicios

### 1️⃣ ms-academic-auth
- **Puerto:** 8081
- **Función:** Autenticación y autorización con JWT
- **Tecnologías:** Spring Security, JWT, PostgreSQL
- **Endpoints principales:**
  - POST `/api/auth/register` - Registro de usuarios
  - POST `/api/auth/login` - Autenticación
  - POST `/api/auth/refresh` - Renovar token
  - GET `/api/auth/validate` - Validar token

### 2️⃣ ms-academic-management
- **Puerto:** 8082
- **Función:** Gestión de estudiantes, cursos y matrículas
- **Tecnologías:** Spring Data JPA, PostgreSQL, RabbitMQ
- **Endpoints principales:**
  - CRUD Estudiantes: `/api/students`
  - CRUD Cursos: `/api/courses`
  - Matrículas: `/api/enrollments`

### 3️⃣ ms-academic-notification
- **Puerto:** 8083
- **Función:** Envío de notificaciones por email
- **Tecnologías:** Spring AMQP, Spring Mail, Thymeleaf
- **Características:**
  - Consume mensajes de RabbitMQ
  - Genera emails HTML profesionales
  - Reintentos automáticos

### 4️⃣ ms-academic-audit
- **Puerto:** 8084
- **Función:** Auditoría y registro histórico de eventos
- **Tecnologías:** Spring Kafka, PostgreSQL, JPA
- **Endpoints principales:**
  - GET `/api/audit/events` - Listar eventos (paginado)
  - GET `/api/audit/events/student/{id}` - Historial por estudiante
  - GET `/api/audit/events/course/{id}` - Historial por curso
  - GET `/api/audit/stats` - Estadísticas generales

## 🚀 Inicio Rápido

### Opción A: Todo en un comando (RECOMENDADO)

```bash
# 1. Copiar y configurar el archivo de variables de entorno
cp .env.example .env
# Editar .env con tus credenciales (MAIL_USERNAME, MAIL_PASSWORD, etc.)

# 2. Construir todas las imágenes
docker-compose -f docker-compose-master.yml build

# 3. Levantar todos los servicios
docker-compose -f docker-compose-master.yml up -d

# 4. Verificar estado
docker-compose -f docker-compose-master.yml ps

# 5. Ver logs
docker-compose -f docker-compose-master.yml logs -f
```

### Opción B: Servicios individuales

```bash
# Levantar solo autenticación
cd ms-academic-auth
docker-compose up -d

# Levantar gestión académica
cd ms-academic-management
docker-compose up -d

# Levantar notificaciones
cd ms-academic-notification
docker-compose up -d
```

## 📋 Prerequisitos

- Docker Desktop
- 8 GB RAM disponible (recomendado por Kafka)
- Puertos libres: 2181, 5433, 5434, 5435, 5672, 8081, 8082, 8083, 8084, 9092, 15672, 29092
- **Nota:** Si tienes PostgreSQL local, puede estar usando el puerto 5432. Este proyecto usa puertos alternativos para evitar conflictos.

## ⚙️ Configuración de Variables de Entorno

El proyecto utiliza un archivo `.env` para configurar credenciales y puertos. **Este paso es obligatorio para el servicio de notificaciones.**

### Pasos:

1. Copiar el archivo de ejemplo:
   ```bash
   cp .env.example .env
   ```

2. Editar `.env` con tus valores:

### Variables Principales

| Variable | Descripción | Requerido |
|----------|-------------|-----------|
| `MAIL_USERNAME` | Usuario SMTP (Mailtrap, Gmail, etc.) | ✅ Sí |
| `MAIL_PASSWORD` | Contraseña SMTP | ✅ Sí |
| `MAIL_HOST` | Host del servidor SMTP | Opcional |
| `MAIL_PORT` | Puerto SMTP | Opcional |
| `JWT_SECRET` | Secreto para firmar tokens (mín. 32 chars) | Recomendado |
| `JWT_ACCESS_EXPIRATION` | Expiración access token (ms) | Opcional |
| `JWT_REFRESH_EXPIRATION` | Expiración refresh token (ms) | Opcional |

### Variables de Base de Datos

| Variable | Descripción | Default |
|----------|-------------|---------|
| `DB_AUTH_USER` | Usuario PostgreSQL Auth | postgres |
| `DB_AUTH_PASSWORD` | Contraseña PostgreSQL Auth | postgres |
| `DB_MANAGEMENT_USER` | Usuario PostgreSQL Management | postgres |
| `DB_MANAGEMENT_PASSWORD` | Contraseña PostgreSQL Management | postgres |
| `DB_AUDIT_USER` | Usuario PostgreSQL Audit | postgres |
| `DB_AUDIT_PASSWORD` | Contraseña PostgreSQL Audit | postgres |

### Variables de RabbitMQ

| Variable | Descripción | Default |
|----------|-------------|---------|
| `RABBITMQ_USER` | Usuario RabbitMQ | admin |
| `RABBITMQ_PASSWORD` | Contraseña RabbitMQ | admin123 |

### Variables de Puertos

Si tienes conflictos con puertos locales, puedes cambiarlos:

| Variable | Descripción | Default |
|----------|-------------|---------|
| `PORT_AUTH` | Puerto ms-auth | 8081 |
| `PORT_MANAGEMENT` | Puerto ms-management | 8082 |
| `PORT_NOTIFICATION` | Puerto ms-notification | 8083 |
| `PORT_AUDIT` | Puerto ms-audit | 8084 |
| `PORT_RABBITMQ_UI` | Puerto RabbitMQ UI | 15672 |
| `PORT_KAFKA_UI` | Puerto Kafka UI | 8080 |
| `PORT_POSTGRES_AUTH` | Puerto PostgreSQL Auth | 5434 |
| `PORT_POSTGRES_MANAGEMENT` | Puerto PostgreSQL Management | 5433 |
| `PORT_POSTGRES_AUDIT` | Puerto PostgreSQL Audit | 5435 |

### Ejemplo de Configuración con Mailtrap

```env
MAIL_HOST=smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=tu_usuario_mailtrap
MAIL_PASSWORD=tu_password_mailtrap
JWT_SECRET=mi_secreto_super_seguro_de_32_caracteres_minimo
```

### Ejemplo de Configuración con Gmail

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password
```

> **Nota:** Para Gmail necesitas crear una "App Password" en la configuración de seguridad de tu cuenta.

## 🧪 Prueba Rápida

```bash
# 1. Login con usuario precargado (contraseña: password123)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"juan.perez","password":"password123"}'

# 2. Crear matrícula (reemplaza TOKEN con el accessToken del paso anterior)
curl -X POST http://localhost:8082/api/enrollments \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"estudianteId":1,"cursoId":1}'

# 3. Ver email enviado en Mailtrap
# Ir a: https://mailtrap.io/inboxes/

# 4. Consultar eventos de auditoría
curl http://localhost:8084/api/audit/stats
```

**Usuarios disponibles (contraseña: `password123`):**
- Estudiantes: `juan.perez`, `maria.gonzalez`, `carlos.rodriguez`, `ana.martinez`, `luis.sanchez`
- Profesor: `prof.garcia`
- Admin: `admin`

## 📊 URLs de Acceso

| Servicio | URL | Usuario/Pass |
|----------|-----|--------------|
| Auth API | http://localhost:8081 | - |
| Management API | http://localhost:8082 | - |
| Notification API | http://localhost:8083 | - |
| Audit API | http://localhost:8084 | - |
| RabbitMQ UI | http://localhost:15672 | admin / admin123 |
| Email Viewer | https://mailtrap.io/inboxes/ | 6abd9f88309e4d / 4ec30b10b54c0b |
| PostgreSQL Auth | localhost:5434 | postgres / postgres |
| PostgreSQL Management | localhost:5433 | postgres / postgres |
| PostgreSQL Audit | localhost:5435 | postgres / postgres |

## 📁 Estructura de los Microservicios

A continuación, se resume la estructura de paquetes de cada microservicio para facilitar la navegación y comprensión del código.

### 1️⃣ ms-academic-auth

-   `config`: Clases de configuración para Spring Security y CORS.
-   `controller`: Endpoints REST para registro, login y validación de tokens.
-   `dto`: Data Transfer Objects para las peticiones y respuestas de la API.
-   `entity`: Modelos de datos JPA (`User`, `Role`).
-   `exception`: Manejo de excepciones personalizadas y un handler global.
-   `repository`: Interfaces de Spring Data JPA para acceder a la base de datos.
-   `security`: Lógica relacionada con JWT (creación, validación) y `UserDetailsService`.
-   `service`: Lógica de negocio para la autenticación de usuarios.

### 2️⃣ ms-academic-management

-   `config`: Configuración de RabbitMQ, RestTemplate, Spring Security y CORS.
-   `controller`: Endpoints REST para la gestión de estudiantes, cursos y matrículas.
-   `dto`: DTOs para las operaciones CRUD y la comunicación con otros servicios.
-   `entity`: Modelos de datos JPA (`Student`, `Course`, `Enrollment`).
-   `exception`: Excepciones de negocio y un handler global.
-   `repository`: Interfaces de Spring Data JPA.
-   `security`: Filtro de autenticación JWT para proteger los endpoints.
-   `service`: Lógica de negocio para la gestión académica y publicación de eventos en RabbitMQ.

### 3️⃣ ms-academic-notification

-   `config`: Configuración de RabbitMQ y el Message Converter.
-   `controller`: Endpoints REST para consultar estadísticas y el estado del servicio.
-   `dto`: DTO para deserializar los mensajes de matrícula desde RabbitMQ.
-   `listener`: Consumidor de RabbitMQ que escucha los eventos de matrícula.
-   `service`: Lógica para procesar los mensajes y enviar emails con Thymeleaf.

### 4️⃣ ms-academic-audit

-   `config`: Configuración del consumidor de Kafka.
-   `controller`: Endpoints REST para consultar eventos y estadísticas de auditoría.
-   `dto`: DTOs para eventos de Kafka y respuestas de la API.
-   `entity`: Modelo de datos JPA (`AuditEvent`).
-   `exception`: Manejo global de excepciones.
-   `listener`: Consumidor de Kafka que escucha eventos de matrículas.
-   `repository`: Interfaz de Spring Data JPA con queries personalizadas.
-   `service`: Lógica para guardar eventos y generar estadísticas.

## 🔄 Flujo Completo

1. **Registro/Login** → Usuario se autentica en `ms-auth`
2. **Obtiene JWT** → Token de acceso válido por 1 hora
3. **Crea Matrícula** → Request a `ms-management` con token
4. **Valida Token** → `ms-management` valida con `ms-auth`
5. **Guarda en BD** → Matrícula se guarda en PostgreSQL
6. **Publica en RabbitMQ** → `ms-management` envía mensaje para notificación
7. **Publica en Kafka** → `ms-management` envía evento para auditoría
8. **Consume RabbitMQ** → `ms-notification` recibe y envía email
9. **Consume Kafka** → `ms-audit` recibe y guarda evento
10. **Consulta Auditoría** → Historial disponible en `/api/audit`

## 📚 Datos Precargados

### Usuarios de Autenticación (ms-academic-auth)
**Contraseña para todos: `password123`**

#### 👨‍🎓 Estudiantes (5):
- `juan.perez` - juan.perez@universidad.edu
- `maria.gonzalez` - maria.gonzalez@universidad.edu
- `carlos.rodriguez` - carlos.rodriguez@universidad.edu
- `ana.martinez` - ana.martinez@universidad.edu
- `luis.sanchez` - luis.sanchez@universidad.edu

#### 👨‍🏫 Profesor (1):
- `prof.garcia` - profesor.garcia@universidad.edu

#### 👨‍💼 Administrador (1):
- `admin` - admin@universidad.edu

### Estudiantes (ms-academic-management)
- E001 - Juan Pérez - juan.perez@universidad.edu
- E002 - María González - maria.gonzalez@universidad.edu
- E003 - Carlos Rodríguez - carlos.rodriguez@universidad.edu
- E004 - Ana Martínez - ana.martinez@universidad.edu
- E005 - Luis Sánchez - luis.sanchez@universidad.edu

### Cursos
- CS101 - Introducción a la Programación (4 créditos, 27/30 cupos)
- CS201 - Estructura de Datos (5 créditos, 23/25 cupos)
- CS301 - Algoritmos Avanzados (5 créditos, 20/20 cupos)
- CS401 - Bases de Datos (4 créditos, 30/30 cupos)
- CS501 - Arquitectura de Software (5 créditos, 15/15 cupos)

**Nota:** Los emails coinciden entre ambos microservicios para que las notificaciones funcionen.

## 🛠️ Tecnologías

- **Backend:** Java 17, Spring Boot 3.2.0
- **Seguridad:** Spring Security, JWT
- **Base de Datos:** PostgreSQL 15
- **Mensajería:** RabbitMQ 3 (notificaciones), Apache Kafka (auditoría)
- **Email:** Spring Mail, Thymeleaf
- **Contenedores:** Docker, Docker Compose
- **Build:** Maven

## 📖 Documentación Detallada

Para documentación de cada microservicio:
- [ms-academic-auth/README.md](./ms-academic-auth/README.md)
- [ms-academic-management/README.md](./ms-academic-management/README.md)
- [ms-academic-notification/README.md](./ms-academic-notification/README.md)
- [ms-academic-audit/README.md](./ms-academic-audit/README.md)

## 🐛 Troubleshooting

```bash
# Ver logs de todos los servicios
docker-compose -f docker-compose-master.yml logs -f

# Ver logs de un servicio específico
docker-compose -f docker-compose-master.yml logs -f ms-management

# Reiniciar un servicio
docker-compose -f docker-compose-master.yml restart ms-notification

# Verificar health checks
docker-compose -f docker-compose-master.yml ps

# Detener todo
docker-compose -f docker-compose-master.yml down

# Limpiar todo (incluyendo volúmenes)
docker-compose -f docker-compose-master.yml down -v
```

## ✅ Checklist de Funcionamiento

- [ ] Todos los contenedores en estado "healthy"
- [ ] Login exitoso y obtención de JWT
- [ ] Listado de estudiantes y cursos funciona
- [ ] Creación de matrícula exitosa
- [ ] Mensaje visible en RabbitMQ Management
- [ ] Email recibido en Mailtrap
- [ ] Evento registrado en ms-audit
- [ ] Estadísticas de auditoría actualizadas

## 🎯 Características Principales

- ✅ Autenticación centralizada con JWT
- ✅ Gestión completa de estudiantes y cursos
- ✅ Sistema de matrículas con validaciones
- ✅ Notificaciones asíncronas vía RabbitMQ
- ✅ Auditoría de eventos vía Kafka
- ✅ Emails HTML profesionales
- ✅ Reintentos automáticos
- ✅ Health checks
- ✅ Logging completo
- ✅ Dockerización completa
- ✅ Datos de ejemplo precargados

## 📝 Licencia

Este es un proyecto demo para fines educativos.

## 👥 Autor

Sistema Académico - Arquitectura de Microservicios
