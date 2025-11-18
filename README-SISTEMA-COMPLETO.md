# Sistema de Matrículas - Microservicios

Sistema académico de gestión de matrículas con arquitectura de microservicios, autenticación JWT, mensajería con RabbitMQ y notificaciones por email.

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                         USUARIO                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
        ┌──────────────────────────────────────┐
        │   ms-academic-auth (Puerto 8081)      │
        │   - Registro de usuarios              │
        │   - Login (JWT)                       │
        │   - Validación de tokens              │
        └──────────────────────────────────────┘
                              │
                              ▼
        ┌──────────────────────────────────────┐
        │ ms-academic-management (Puerto 8082)  │
        │   - Gestión de estudiantes            │
        │   - Gestión de cursos                 │
        │   - Gestión de matrículas             │
        └──────────────────────────────────────┘
                              │
                              ▼ (Publica mensaje)
        ┌──────────────────────────────────────┐
        │        RabbitMQ (Puertos 5672/15672)  │
        └──────────────────────────────────────┘
                              │
                              ▼ (Consume mensaje)
        ┌──────────────────────────────────────┐
        │ ms-academic-notification (Puerto 8083)│
        │   - Consume eventos de matrícula      │
        │   - Envía emails HTML                 │
        └──────────────────────────────────────┘
```

## 📋 Prerequisitos

- Docker Desktop instalado
- Java 17 o superior (para desarrollo)
- Maven (para desarrollo)
- Git

## 🚀 Instalación y Ejecución

### Paso 1: Clonar o tener los microservicios

Asegúrate de tener esta estructura de carpetas:

```
proyecto-matriculas/
├── ms-academic-auth/
├── ms-academic-management/
├── ms-academic-notification/
└── docker-compose.yml (el archivo maestro)
```

### Paso 2: Construir las imágenes

```bash
docker-compose build
```

Este comando construirá las imágenes de los 3 microservicios.

### Paso 3: Levantar todos los servicios

```bash
docker-compose up -d
```

Esto levantará:
- 2 bases de datos PostgreSQL (auth y management)
- RabbitMQ
- 3 microservicios (auth, management, notification)

### Paso 4: Verificar que todo esté corriendo

```bash
docker-compose ps
```

Deberías ver 6 contenedores corriendo:
- postgres-auth
- postgres-management
- rabbitmq
- ms-auth
- ms-management
- ms-notification

## 📡 URLs de Acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Autenticación | http://localhost:8081 | API de autenticación |
| Gestión Académica | http://localhost:8082 | API de matrículas |
| Notificaciones | http://localhost:8083 | API de notificaciones |
| RabbitMQ Management | http://localhost:15672 | UI de RabbitMQ (admin/admin123) |
| Ethereal Email | https://ethereal.email/messages | Ver emails enviados |

## 🧪 Pruebas con Postman/cURL

### 1. Registrar un usuario

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "email": "juan.perez@university.edu",
    "password": "password123",
    "role": "STUDENT"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "password": "password123"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

**IMPORTANTE:** Guarda el token para las siguientes peticiones.

### 3. Ver cursos disponibles

```bash
curl -X GET http://localhost:8082/api/courses \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

### 4. Matricularse en un curso

```bash
curl -X POST http://localhost:8082/api/enrollments \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "courseId": 1
  }'
```

**¡Esto enviará un email automáticamente!** 🎉

### 5. Ver el email enviado

1. Ve a: https://ethereal.email/messages
2. Login con:
   - Usuario: gregorio.oconner76@ethereal.email
   - Password: Cjxq14JGFPCZM72psn
3. Verás el email HTML con la confirmación de matrícula

## 📊 Monitoreo

### Ver logs de un servicio específico

```bash
# Ver logs del servicio de autenticación
docker-compose logs -f ms-auth

# Ver logs del servicio de gestión académica
docker-compose logs -f ms-management

# Ver logs del servicio de notificaciones
docker-compose logs -f ms-notification

# Ver todos los logs
docker-compose logs -f
```

### Ver cola de RabbitMQ

1. Abre http://localhost:15672
2. Login: admin / admin123
3. Ve a "Queues" para ver los mensajes

## 🛑 Detener los servicios

```bash
# Detener servicios pero mantener datos
docker-compose stop

# Detener y eliminar contenedores
docker-compose down

# Detener, eliminar contenedores Y volúmenes (CUIDADO: borra las BDs)
docker-compose down -v
```

## 🔄 Reiniciar un servicio específico

```bash
# Reiniciar el servicio de notificaciones
docker-compose restart ms-notification

# Reconstruir y reiniciar
docker-compose up -d --build ms-notification
```

## 🐛 Troubleshooting

### Problema: Servicios no se conectan

**Solución:** Verifica que todos estén en la misma red:
```bash
docker network inspect proyecto-matriculas_academic-network
```

### Problema: RabbitMQ no recibe mensajes

**Solución:** 
1. Verifica que RabbitMQ esté corriendo: `docker-compose ps rabbitmq`
2. Revisa logs: `docker-compose logs -f rabbitmq`
3. Verifica la UI: http://localhost:15672

### Problema: No se envían emails

**Solución:**
1. Verifica logs del ms-notification: `docker-compose logs -f ms-notification`
2. Verifica credenciales de Ethereal en docker-compose.yml
3. Prueba las credenciales en https://ethereal.email/

### Problema: Error de base de datos

**Solución:**
```bash
# Reiniciar bases de datos
docker-compose restart postgres-auth postgres-management

# Si no funciona, eliminar volúmenes y recrear
docker-compose down -v
docker-compose up -d
```

## 📦 Datos de Prueba

El sistema viene con datos precargados:

**Estudiantes:** 5 estudiantes de ejemplo
**Cursos:** 5 cursos con diferentes créditos
**Matrículas:** 3 matrículas de ejemplo

Puedes ver estos datos haciendo:
```bash
curl -X GET http://localhost:8082/api/students \
  -H "Authorization: Bearer TU_TOKEN"
```

## 🔐 Seguridad

- Todos los endpoints de ms-management requieren token JWT
- Los tokens expiran en 24 horas
- Las contraseñas se almacenan con BCrypt
- CORS configurado para desarrollo

## 📝 Notas Importantes

1. **Orden de inicio:** Los servicios tienen dependencias configuradas (depends_on) para iniciar en el orden correcto
2. **Health checks:** Cada servicio tiene health checks para asegurar disponibilidad
3. **Reintentos:** El ms-notification reintenta enviar emails hasta 3 veces si falla
4. **Emails:** Se envían a Ethereal (no son emails reales, solo para testing)

## 🎯 Flujo Completo de Matrícula

1. Usuario se registra → `ms-auth`
2. Usuario hace login → `ms-auth` devuelve JWT
3. Usuario lista cursos → `ms-management` (valida JWT con ms-auth)
4. Usuario se matricula → `ms-management`:
   - Valida JWT
   - Crea matrícula en BD
   - Publica mensaje en RabbitMQ
5. RabbitMQ entrega mensaje → `ms-notification`
6. `ms-notification`:
   - Consume mensaje
   - Genera HTML
   - Envía email

## 🆘 Soporte

Si tienes problemas:
1. Revisa los logs: `docker-compose logs -f`
2. Verifica el estado: `docker-compose ps`
3. Reinicia los servicios: `docker-compose restart`

---

**¡Sistema listo para usar!** 🚀
