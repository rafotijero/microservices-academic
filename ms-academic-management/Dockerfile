# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir el proyecto
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no privilegiado
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar al usuario no privilegiado
USER spring:spring

# Exponer el puerto
EXPOSE 8082

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
