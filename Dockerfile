# Etapa 1: Compilar el proyecto con Gradle
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de configuración de Gradle para aprovechar la caché de Docker
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Dar permisos de ejecución al script de Gradle
RUN chmod +x gradlew

# Descargar dependencias
RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente y compilar el JAR
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Etapa 2: Imagen liviana para ejecutar la aplicación
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el ejecutable generado desde la etapa de compilación
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]