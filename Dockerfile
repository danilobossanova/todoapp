FROM eclipse-temurin:21-alpine AS builder
VOLUME /tmp
WORKDIR /app
COPY target/todoapp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
