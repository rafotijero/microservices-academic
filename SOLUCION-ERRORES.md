# 🔧 Solución de Errores Comunes

## Error: "Connection to localhost:5432 refused" en ms-auth

### ❌ **Problema:**
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

### ✅ **Causas y Soluciones:**

#### Causa 0: Puerto 5432 ocupado por PostgreSQL local ⭐ **MÁS COMÚN**

**Problema:** Tienes PostgreSQL instalado localmente y está usando el puerto 5432.

**Solución A - Ya está resuelto en docker-compose-master.yml:**
El archivo maestro ya usa el puerto 5434 para evitar este conflicto. Solo ejecuta:

```bash
docker-compose -f docker-compose-master.yml down
docker-compose -f docker-compose-master.yml up -d
```

**Solución B - Detener PostgreSQL local temporalmente (Windows):**

```bash
# Ver si PostgreSQL está corriendo
netstat -ano | findstr ":5432"

# Detener el servicio de PostgreSQL
net stop postgresql-x64-15

# O desde Servicios de Windows:
# 1. Presiona Win + R
# 2. Escribe "services.msc"
# 3. Busca "PostgreSQL"
# 4. Click derecho > Detener
```

**Solución C - Verificar qué proceso usa el puerto:**

```bash
# Ver qué proceso ocupa 5432
netstat -ano | findstr ":5432"
# Resultado: mostrará el PID (ej: 6428)

# Ver qué programa es
tasklist | findstr "6428"
```

#### Causa 1: Estás ejecutando servicios individuales

**Problema:** Si ejecutas los servicios con sus propios `docker-compose.yml` individuales, usan configuraciones diferentes.

**Solución:** Usa el archivo maestro para ejecutar TODO:

```bash
# ❌ NO HAGAS ESTO:
cd ms-academic-auth
docker-compose up -d

# ✅ HAZ ESTO:
cd /ruta/al/proyecto/microservices
docker-compose -f docker-compose-master.yml down
docker-compose -f docker-compose-master.yml build
docker-compose -f docker-compose-master.yml up -d
```

#### Causa 2: Servicios ya ejecutándose

**Problema:** Si hay contenedores de ejecuciones previas, pueden estar interfiriendo.

**Solución:** Detén y limpia todo antes de iniciar:

```bash
# Detener todos los contenedores relacionados
docker stop ms-auth ms-management ms-notification postgres-auth postgres-management rabbitmq 2>/dev/null

# Eliminar contenedores
docker rm ms-auth ms-management ms-notification postgres-auth postgres-management rabbitmq 2>/dev/null

# Limpiar (CUIDADO: borra los datos)
docker-compose -f docker-compose-master.yml down -v

# Reconstruir e iniciar
docker-compose -f docker-compose-master.yml build
docker-compose -f docker-compose-master.yml up -d
```

#### Causa 3: Variables de entorno no se pasan correctamente

**Problema:** Las variables de entorno del docker-compose no llegan al contenedor.

**Solución:** Verifica que las variables estén configuradas:

```bash
# Ver las variables de entorno del contenedor
docker exec ms-auth env | grep DB_

# Deberías ver:
# DB_HOST=postgres-auth
# DB_PORT=5432
# DB_NAME=auth_db
# DB_USER=postgres
# DB_PASSWORD=postgres
```

Si no ves estas variables, reconstruye:
```bash
docker-compose -f docker-compose-master.yml build ms-auth
docker-compose -f docker-compose-master.yml up -d ms-auth
```

---

## Error: "No se pueden crear las tablas" o "Table already exists"

### ✅ **Solución:**

El microservicio usa `ddl-auto: update` que actualiza el esquema automáticamente. Si tienes problemas:

```bash
# Opción 1: Recrear solo la base de datos
docker-compose -f docker-compose-master.yml stop postgres-auth
docker volume rm microservices_postgres-auth-data
docker-compose -f docker-compose-master.yml up -d postgres-auth
docker-compose -f docker-compose-master.yml restart ms-auth

# Opción 2: Recrear todo
docker-compose -f docker-compose-master.yml down -v
docker-compose -f docker-compose-master.yml up -d
```

---

## Error: "Could not open init.sql"

### ✅ **Solución:**

El archivo `init.sql` debe estar en `src/main/resources/`. Verifica:

```bash
# Verificar que existe
ls ms-academic-auth/src/main/resources/init.sql

# Si no existe, reconstruye el proyecto
docker-compose -f docker-compose-master.yml build ms-auth
```

---

## Error: Health check failing

### ✅ **Solución:**

```bash
# Ver logs del servicio
docker-compose -f docker-compose-master.yml logs ms-auth

# Ver estado de salud
docker-compose -f docker-compose-master.yml ps

# Verificar manualmente
docker exec ms-auth wget -O- http://localhost:8081/actuator/health
```

---

## Error: "Network academic-network not found"

### ✅ **Solución:**

```bash
# Crear la red manualmente
docker network create academic-network

# O usar docker-compose que la crea automáticamente
docker-compose -f docker-compose-master.yml up -d
```

---

## Comandos de Diagnóstico

### Ver logs en tiempo real
```bash
# Todos los servicios
docker-compose -f docker-compose-master.yml logs -f

# Solo ms-auth
docker-compose -f docker-compose-master.yml logs -f ms-auth

# Últimas 50 líneas
docker-compose -f docker-compose-master.yml logs --tail=50 ms-auth
```

### Ver estado de contenedores
```bash
# Estado de salud
docker-compose -f docker-compose-master.yml ps

# Detalles del contenedor
docker inspect ms-auth

# Conectividad de red
docker exec ms-auth ping -c 2 postgres-auth
```

### Verificar base de datos
```bash
# Conectarse a PostgreSQL
docker exec -it postgres-auth psql -U postgres -d auth_db

# Ver tablas
\dt

# Ver usuarios
SELECT * FROM users;

# Salir
\q
```

### Ver variables de entorno
```bash
# Todas las variables
docker exec ms-auth env

# Solo DB
docker exec ms-auth env | grep DB_

# Solo Spring
docker exec ms-auth env | grep SPRING_
```

---

## Reinicio Limpio Total

Si nada funciona, haz un reinicio completo:

```bash
# 1. Detener todo
docker-compose -f docker-compose-master.yml down

# 2. Eliminar volúmenes (BORRA TODOS LOS DATOS)
docker volume prune -f

# 3. Eliminar red
docker network rm academic-network 2>/dev/null

# 4. Limpiar caché de construcción
docker builder prune -f

# 5. Reconstruir desde cero
docker-compose -f docker-compose-master.yml build --no-cache

# 6. Iniciar todo
docker-compose -f docker-compose-master.yml up -d

# 7. Ver logs
docker-compose -f docker-compose-master.yml logs -f
```

---

## Verificación Paso a Paso

```bash
# 1. Verificar que Docker está corriendo
docker ps

# 2. Verificar imágenes
docker images | grep ms-

# 3. Verificar red
docker network ls | grep academic

# 4. Iniciar solo bases de datos primero
docker-compose -f docker-compose-master.yml up -d postgres-auth postgres-management rabbitmq

# 5. Esperar a que estén healthy (30 segundos)
sleep 30

# 6. Verificar que están healthy
docker-compose -f docker-compose-master.yml ps

# 7. Iniciar servicios
docker-compose -f docker-compose-master.yml up -d ms-auth ms-management ms-notification

# 8. Ver logs
docker-compose -f docker-compose-master.yml logs -f
```

---

## Contacto y Soporte

Si el problema persiste después de probar estas soluciones:

1. Copia los logs completos: `docker-compose -f docker-compose-master.yml logs > logs.txt`
2. Verifica las versiones: `docker --version` y `docker-compose --version`
3. Revisa el uso de recursos: `docker stats`
