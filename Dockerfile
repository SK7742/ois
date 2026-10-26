# ==============================================================================
# Multi-Stage Dockerfile for Oxford International School - CSMS
# Production-ready cloud deployment for Render, Railway, Fly.io, etc.
# ==============================================================================

# Stage 1: Build the Spring Boot application from source
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy project configuration and fee structure
COPY backend/pom.xml backend/pom.xml
COPY .fee-structure-2026 .fee-structure-2026

# Copy source code and compile package
COPY backend/src backend/src
RUN mvn -f backend/pom.xml clean package -DskipTests

# Stage 2: Minimal JRE 21 Runtime Container
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy packaged fat JAR and fee structure from builder stage
COPY --from=builder /app/backend/target/oxford-csms-backend-1.0.0-SNAPSHOT.jar app.jar
COPY --from=builder /app/.fee-structure-2026 .fee-structure-2026

# Cloud platforms like Render dynamically assign PORT
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
