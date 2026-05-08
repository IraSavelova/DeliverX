FROM maven:3.9.11-eclipse-temurin-25-alpine AS builder
WORKDIR /build
COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw ./
COPY src ./src
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
