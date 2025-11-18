# 🚀 Guía de Ejecución - Sistema de Microservicios Académicos

## 📋 Descripción General

Este sistema está compuesto por 3 microservicios que trabajan juntos:

1. **ms-academic-auth** (Puerto 8081) - Autenticación con JWT
2. **ms-academic-management** (Puerto 8082) - Gestión de estudiantes, cursos y matrículas
3. **ms-academic-notification** (Puerto 8083) - Notificaciones por email vía RabbitMQ

## 🔧 Prerequisitos

- Docker Desktop instalado y ejecutándose
- Al menos 4 GB de RAM disponible
- Puertos libres: 5434, 5433, 5672, 8081, 8082, 8083, 15672
- **IMPORTANTE:** Si tienes PostgreSQL instalado localmente, detenlo o este proyecto usará el puerto 5434

## 🎯 Método 1: Ejecutar TODO con un solo comando (RECOMENDADO)

### Paso 1: Construir todas las imágenes

```bash
docker-compose -f docker-compose-master.yml build
```

⏱️ Este proceso puede tardar 5-10 minutos la primera vez.

### Paso 2: Levantar todos los servicios

```bash
docker-compose -f docker-compose-master.yml up -d
```

### Paso 3: Verificar que todo esté corriendo

```bash
docker-compose -f docker-compose-master.yml ps
```

Deberías ver 6 contenedores:
- ✅ postgres-auth (healthy)
- ✅ postgres-management (healthy)
- ✅ rabbitmq (healthy)
- ✅ ms-auth (healthy)
- ✅ ms-management (healthy)
- ✅ ms-notification (healthy)

### Paso 4: Ver logs en tiempo real

```bash
# Ver todos los logs
docker-compose -f docker-compose-master.yml logs -f

# Ver logs de un servicio específico
docker-compose -f docker-compose-master.yml logs -f ms-auth
docker-compose -f docker-compose-master.yml logs -f ms-management
docker-compose -f docker-compose-master.yml logs -f ms-notification
```

### Paso 5: Detener todo

```bash
docker-compose -f docker-compose-master.yml down
```

Para eliminar también los volúmenes (CUIDADO: borra todos los datos):
```bash
docker-compose -f docker-compose-master.yml down -v
```

---

## 🧪 Probar la Funcionalidad Completa

### Test 1: Health Checks

```bash
# Verificar servicio de autenticación
curl http://localhost:8081/actuator/health

# Verificar servicio de gestión académica
curl http://localhost:8082/actuator/health

# Verificar servicio de notificaciones
curl http://localhost:8083/api/notifications/health
```

### Test 2: Login con usuarios precargados

Ya hay 7 usuarios creados automáticamente. Todos tienen la contraseña: `password123`

**Opción A - Login como Estudiante:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "juan.perez",
    "password": "password123"
  }'
```

**Opción B - Login como Profesor:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "prof.garcia",
    "password": "password123"
  }'
```

**Opción C - Login como Admin:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "password123"
  }'
```

**Guarda el `accessToken` que retorna para usarlo en los siguientes pasos.**

### Test 3: (Opcional) Registrar un nuevo usuario

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo.estudiante",
    "email": "nuevo@universidad.edu",
    "password": "password123",
    "roles": ["ROLE_STUDENT"]
  }'
```

### Test 4: Listar estudiantes (con autenticación)

```bash
curl -X GET http://localhost:8082/api/students \
  -H "Authorization: Bearer TU_ACCESS_TOKEN_AQUI"
```

### Test 5: Listar cursos

```bash
curl -X GET http://localhost:8082/api/courses \
  -H "Authorization: Bearer TU_ACCESS_TOKEN_AQUI"
```

### Test 6: Crear una matrícula (ESTO ENVIARÁ UN EMAIL) ⭐

```bash
curl -X POST http://localhost:8082/api/enrollments \
  -H "Authorization: Bearer TU_ACCESS_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "estudianteId": 1,
    "cursoId": 1
  }'
```

**¿Qué debe pasar?**
1. Se crea la matrícula en la base de datos
2. Se publica un mensaje en RabbitMQ
3. El servicio de notificaciones recibe el mensaje
4. Se envía un email HTML al estudiante

### Test 7: Ver el email enviado

1. Abre tu navegador en: https://ethereal.email/messages
2. Click en "Login"
3. Credenciales:
   - Email: `gregorio.oconner76@ethereal.email`
   - Password: `Cjxq14JGFPCZM72psn`
4. Deberías ver el email de confirmación de matrícula

### Test 8: Ver estadísticas de notificaciones

```bash
curl http://localhost:8083/api/notifications/stats
```

### Test 9: Ver RabbitMQ Management UI

1. Abre: http://localhost:15672
2. Login: `admin` / `admin123`
3. Ve a "Queues" para ver la cola `enrollment.notification.queue`
4. Ve a "Exchanges" para ver `enrollment.exchange`

---

## 📊 URLs de Acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Auth Service | http://localhost:8081 | Endpoints de autenticación |
| Management Service | http://localhost:8082 | Endpoints de gestión académica |
| Notification Service | http://localhost:8083 | Endpoints de notificaciones |
| RabbitMQ Management | http://localhost:15672 | UI de administración (admin/admin123) |
| Ethereal Email | https://ethereal.email/messages | Ver emails enviados |
| PostgreSQL Auth | localhost:5434 | Base de datos de autenticación (postgres/postgres) |
| PostgreSQL Management | localhost:5433 | Base de datos académica (postgres/postgres) |

---

## 🐛 Troubleshooting

### Problema: Los contenedores no inician

```bash
# Ver logs detallados
docker-compose -f docker-compose-master.yml logs

# Verificar que no haya problemas de puertos
docker ps -a
netstat -an | findstr "8081 8082 8083 5432 5433 5672"
```

### Problema: Health checks fallan

```bash
# Esperar más tiempo (los servicios tardan ~1 minuto en iniciar)
docker-compose -f docker-compose-master.yml ps

# Reiniciar un servicio específico
docker-compose -f docker-compose-master.yml restart ms-auth
```

### Problema: No se envían emails

```bash
# Ver logs del servicio de notificaciones
docker-compose -f docker-compose-master.yml logs -f ms-notification

# Verificar que RabbitMQ esté corriendo
docker-compose -f docker-compose-master.yml ps rabbitmq

# Ver mensajes en RabbitMQ
# Ir a http://localhost:15672 > Queues > enrollment.notification.queue
```

### Problema: Error "Connection refused" en RabbitMQ

```bash
# Verificar que RabbitMQ esté healthy
docker-compose -f docker-compose-master.yml ps rabbitmq

# Reiniciar RabbitMQ
docker-compose -f docker-compose-master.yml restart rabbitmq

# Esperar 30 segundos y reiniciar los servicios que dependen de él
docker-compose -f docker-compose-master.yml restart ms-management ms-notification
```

### Problema: "Token invalid" o errores de autenticación

El token JWT expira en 1 hora. Genera uno nuevo haciendo login nuevamente.

---

## 🔄 Flujo Completo del Sistema

```
1. Usuario se registra → ms-auth → BD auth_db
2. Usuario hace login → ms-auth → Retorna JWT
3. Usuario crea matrícula → ms-management (valida JWT con ms-auth)
4. ms-management guarda matrícula → BD academic_db
5. ms-management publica mensaje → RabbitMQ
6. ms-notification recibe mensaje → RabbitMQ
7. ms-notification genera email HTML → Plantilla Thymeleaf
8. ms-notification envía email → SMTP Ethereal
9. Usuario ve email → https://ethereal.email/messages
```

---

## 📦 Datos Precargados

### Usuarios en ms-academic-auth (7)
**Contraseña para todos: `password123`**

#### Estudiantes (5):
- **Username:** `juan.perez` | **Email:** juan.perez@universidad.edu | **Rol:** ROLE_STUDENT
- **Username:** `maria.gonzalez` | **Email:** maria.gonzalez@universidad.edu | **Rol:** ROLE_STUDENT
- **Username:** `carlos.rodriguez` | **Email:** carlos.rodriguez@universidad.edu | **Rol:** ROLE_STUDENT
- **Username:** `ana.martinez` | **Email:** ana.martinez@universidad.edu | **Rol:** ROLE_STUDENT
- **Username:** `luis.sanchez` | **Email:** luis.sanchez@universidad.edu | **Rol:** ROLE_STUDENT

#### Profesor (1):
- **Username:** `prof.garcia` | **Email:** profesor.garcia@universidad.edu | **Rol:** ROLE_TEACHER

#### Administrador (1):
- **Username:** `admin` | **Email:** admin@universidad.edu | **Rol:** ROLE_ADMIN

### Estudiantes en ms-academic-management (5):
- E001 - Juan Pérez - Ingeniería de Sistemas
- E002 - María González - Ingeniería de Software
- E003 - Carlos Rodríguez - Ciencias de la Computación
- E004 - Ana Martínez - Ingeniería de Sistemas
- E005 - Luis Sánchez - Ingeniería de Software

**Nota:** Los emails coinciden entre ambos microservicios para que las notificaciones funcionen correctamente.

### Cursos (5):
- CS101 - Introducción a la Programación (4 créditos, 27/30 cupos)
- CS201 - Estructura de Datos (5 créditos, 23/25 cupos)
- CS301 - Algoritmos Avanzados (5 créditos, 20/20 cupos)
- CS401 - Bases de Datos (4 créditos, 30/30 cupos)
- CS501 - Arquitectura de Software (5 créditos, 15/15 cupos)

### Matrículas (3):
- Juan Pérez en CS101
- María González en CS101
- Carlos Rodríguez en CS201

---

## 🎓 Colección de Postman

Para facilitar las pruebas, puedes usar estos endpoints:

### Importar en Postman

Crea una colección con estas variables:
- `base_url_auth`: http://localhost:8081
- `base_url_management`: http://localhost:8082
- `base_url_notification`: http://localhost:8083
- `access_token`: (se actualiza después del login)

---

## 📝 Comandos Útiles

```bash
# Reconstruir un servicio específico
docker-compose -f docker-compose-master.yml build ms-management

# Ver uso de recursos
docker stats

# Limpiar todo (contenedores, imágenes, volúmenes)
docker-compose -f docker-compose-master.yml down -v --rmi all

# Acceder a la shell de un contenedor
docker exec -it ms-management sh

# Ver logs de las últimas 100 líneas
docker-compose -f docker-compose-master.yml logs --tail=100

# Seguir logs en tiempo real filtrando por texto
docker-compose -f docker-compose-master.yml logs -f | grep "ERROR"
```

---

## ✅ Checklist de Verificación

- [ ] Todos los contenedores están en estado "healthy"
- [ ] Puedo hacer login y obtener un token
- [ ] Puedo listar estudiantes y cursos
- [ ] Puedo crear una matrícula
- [ ] Veo el mensaje en RabbitMQ Management
- [ ] Recibo el email en Ethereal
- [ ] Las estadísticas de notificaciones aumentan

---

## 🎉 ¡Listo!

Si completaste todos los pasos, tu sistema de microservicios está funcionando correctamente.

Para cualquier problema, revisa los logs con:
```bash
docker-compose -f docker-compose-master.yml logs -f
```
