# 🔐 Credenciales y Usuarios del Sistema

## 👤 Usuarios de Autenticación (ms-academic-auth)

### Contraseña para TODOS los usuarios: `password123`

### 👨‍🎓 Estudiantes (ROLE_STUDENT)

| Username | Email | Rol |
|----------|-------|-----|
| `juan.perez` | juan.perez@universidad.edu | ROLE_STUDENT |
| `maria.gonzalez` | maria.gonzalez@universidad.edu | ROLE_STUDENT |
| `carlos.rodriguez` | carlos.rodriguez@universidad.edu | ROLE_STUDENT |
| `ana.martinez` | ana.martinez@universidad.edu | ROLE_STUDENT |
| `luis.sanchez` | luis.sanchez@universidad.edu | ROLE_STUDENT |

### 👨‍🏫 Profesor (ROLE_TEACHER)

| Username | Email | Rol |
|----------|-------|-----|
| `prof.garcia` | profesor.garcia@universidad.edu | ROLE_TEACHER |

### 👨‍💼 Administrador (ROLE_ADMIN)

| Username | Email | Rol |
|----------|-------|-----|
| `admin` | admin@universidad.edu | ROLE_ADMIN |

---

## 🗄️ Base de Datos

### PostgreSQL - Auth Database
- **Host:** localhost
- **Puerto:** 5434 (cambiado de 5432 para evitar conflictos con PostgreSQL local)
- **Base de datos:** auth_db
- **Usuario:** postgres
- **Contraseña:** postgres

### PostgreSQL - Management Database
- **Host:** localhost
- **Puerto:** 5433
- **Base de datos:** academic_db
- **Usuario:** postgres
- **Contraseña:** postgres

---

## 🐰 RabbitMQ

### Management UI
- **URL:** http://localhost:15672
- **Usuario:** admin
- **Contraseña:** admin123

### AMQP Connection
- **Host:** localhost
- **Puerto:** 5672
- **Usuario:** admin
- **Contraseña:** admin123

---

## 📧 Email (Ethereal)

### Ver emails enviados
- **URL:** https://ethereal.email/messages
- **Email:** gregorio.oconner76@ethereal.email
- **Contraseña:** Cjxq14JGFPCZM72psn

### Configuración SMTP
- **Host:** smtp.ethereal.email
- **Puerto:** 587
- **Usuario:** gregorio.oconner76@ethereal.email
- **Contraseña:** Cjxq14JGFPCZM72psn

---

## 🧪 Ejemplos de Login

### Login como Estudiante
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "juan.perez",
    "password": "password123"
  }'
```

### Login como Profesor
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "prof.garcia",
    "password": "password123"
  }'
```

### Login como Admin
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "password123"
  }'
```

---

## 📝 Notas Importantes

1. **Los emails coinciden:** Los 5 estudiantes tienen el mismo email tanto en ms-academic-auth como en ms-academic-management para que las notificaciones funcionen correctamente.

2. **Hash BCrypt:** Todas las contraseñas están hasheadas con BCrypt. El hash de `password123` es:
   ```
   $2a$10$N9qo8uLOickgx2ZMRZoMye1J8ZJqMfqSfHkBvGvLvJfBbq5lNGnxO
   ```

3. **Tokens JWT:** Los tokens de acceso expiran en 1 hora. Los refresh tokens expiran en 24 horas.

4. **Demo purposes:** Este es un proyecto de demostración. En producción, debes usar contraseñas más seguras y no compartir credenciales.
