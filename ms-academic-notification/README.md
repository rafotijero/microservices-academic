# Microservicio de Notificaciones - ms-academic-notification

Microservicio de notificaciones que consume mensajes de RabbitMQ y envía emails HTML de confirmación de matrículas.

## Tecnologías

- Java 17
- Spring Boot 3.2.0
- Spring AMQP (RabbitMQ)
- Spring Mail (JavaMailSender)
- Thymeleaf
- Docker & Docker Compose
- Maven

## Funcionalidades

- Consumir mensajes de RabbitMQ cuando se crea una matrícula
- Enviar emails HTML profesionales con confirmación de matrícula
- Health checks y estadísticas de emails enviados
- Reintentos automáticos (máximo 3 intentos)

## Configuración de RabbitMQ

- **Exchange**: enrollment.exchange (tipo: direct)
- **Queue**: enrollment.notification.queue
- **Routing Key**: enrollment.created
- **Usuario**: admin / admin123
- **Puerto**: 5672
- **Management UI**: http://localhost:15672

## Configuración de Email (Ethereal)

- **Host**: smtp.ethereal.email
- **Puerto**: 587
- **Usuario**: gregorio.oconner76@ethereal.email
- **Ver emails enviados**: https://ethereal.email/messages

## Estructura del Mensaje (JSON)

```json
{
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

## Prerequisitos

1. **Docker Desktop** instalado y ejecutándose
2. **Red Docker compartida** creada:
   ```bash
   docker network create academic-network
   ```

## Instrucciones de Ejecución

### 1. Construir la imagen

```bash
cd ms-academic-notification
docker-compose build
```

### 2. Levantar los servicios

```bash
docker-compose up -d
```

### 3. Verificar que los servicios estén corriendo

```bash
docker-compose ps
```

### 4. Ver logs en tiempo real

```bash
docker-compose logs -f ms-notification
```

### 5. Detener los servicios

```bash
docker-compose down
```

## Endpoints

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/notifications/health` | GET | Health check del servicio |
| `/api/notifications/stats` | GET | Estadísticas (emails enviados/fallidos) |

### Ejemplos de uso

**Health Check:**
```bash
curl http://localhost:8083/api/notifications/health
```

**Estadísticas:**
```bash
curl http://localhost:8083/api/notifications/stats
```

## URLs de Acceso

- **Aplicación**: http://localhost:8083
- **RabbitMQ Management**: http://localhost:15672 (admin / admin123)
- **Ver emails enviados**: https://ethereal.email/messages

## Probar el Envío de Emails

### Opción 1: Usando RabbitMQ Management UI

1. Acceder a http://localhost:15672
2. Login: admin / admin123
3. Ir a "Queues" > "enrollment.notification.queue"
4. Click en "Publish message"
5. Pegar el JSON de ejemplo en el payload
6. Click "Publish message"
7. Verificar logs: `docker-compose logs -f ms-notification`
8. Ver email en: https://ethereal.email/messages

### Opción 2: Desde el microservicio de gestión académica

El microservicio ms-academic-management debe publicar mensajes a RabbitMQ cuando se crea una matrícula.

## Estructura del Proyecto

```
ms-academic-notification/
├── src/main/java/com/proyecto/notification/
│   ├── config/
│   │   └── RabbitMQConfig.java          # Configuración de RabbitMQ
│   ├── listener/
│   │   └── EnrollmentListener.java      # Listener de mensajes
│   ├── service/
│   │   └── EmailService.java            # Servicio de envío de emails
│   ├── dto/
│   │   └── EnrollmentMessageDTO.java    # DTO del mensaje
│   ├── controller/
│   │   └── NotificationController.java  # Endpoints de monitoreo
│   └── NotificationApplication.java     # Clase principal
├── src/main/resources/
│   ├── templates/
│   │   └── enrollment-email.html        # Plantilla HTML del email
│   ├── application.yml
│   └── application-dev.yml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Características

- **Email HTML vistoso**: Diseño profesional con colores universitarios
- **Reintentos automáticos**: Hasta 3 intentos en caso de fallo
- **Logging detallado**: Información completa de cada operación
- **Health checks**: Verificación automática de disponibilidad
- **Estadísticas**: Contador de emails enviados y fallidos
- **Stateless**: No requiere base de datos

## Troubleshooting

### El servicio no puede conectarse a RabbitMQ

Verificar que RabbitMQ esté corriendo:
```bash
docker-compose ps rabbitmq
```

Ver logs de RabbitMQ:
```bash
docker-compose logs rabbitmq
```

### Los emails no se envían

1. Verificar logs del servicio:
   ```bash
   docker-compose logs -f ms-notification
   ```

2. Verificar que las credenciales de Ethereal sean correctas

3. Verificar la conectividad SMTP

### No se reciben mensajes de RabbitMQ

1. Verificar que el exchange y queue existan en RabbitMQ Management
2. Verificar que el binding esté configurado correctamente
3. Revisar los logs del listener

## Integración con otros Microservicios

Este servicio está diseñado para integrarse con `ms-academic-management`. Cuando se crea una matrícula en el sistema académico, debe publicar un mensaje en RabbitMQ que este servicio consumirá para enviar el email de confirmación.

### Configuración de la red compartida

Ambos servicios deben estar en la misma red Docker:

```yaml
networks:
  academic-network:
    external: true
```

## Autor

Sistema Académico - Microservicios
