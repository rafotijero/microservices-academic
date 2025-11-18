# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar el pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear un usuario no root para ejecutar la aplicación
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Exponer el puerto
EXPOSE 8081

# Variables de entorno por defecto
ENV SPRING_PROFILE=prod

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
