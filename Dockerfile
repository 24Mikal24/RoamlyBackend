FROM openjdk:21-jdk-slim AS build

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar roamly-backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/roamly-backend.jar"]