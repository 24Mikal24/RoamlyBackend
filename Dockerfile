FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/roamly-backend-0.0.1-SNAPSHOT.jar /app/roamly-backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/roamly-backend.jar"]