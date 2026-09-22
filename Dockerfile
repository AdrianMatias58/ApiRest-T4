# --- Etapa 1: Construcción (Build) ---
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Dar permisos de ejecución al script gradlew
RUN chmod +x gradlew

# Copiar el código fuente
COPY src src

# Compilar omitiendo las pruebas unitarias
RUN ./gradlew build -x test --no-daemon

# --- Etapa 2: Imagen final de ejecución (Runtime) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar únicamente el archivo JAR generado desde la etapa de compilación
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Variables de entorno por defecto (puedes sobrescribirlas al correr el contenedor)
ENV JAVA_OPTS=""

# Comando para arrancar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]