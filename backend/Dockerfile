# Lightweight JRE 21 Runtime Image for Oxford CSMS
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
VOLUME /tmp
COPY target/oxford-csms-backend-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
