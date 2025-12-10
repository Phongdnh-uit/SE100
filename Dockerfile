#STAGE 1: BUILD
FROM gradle:8.10-jdk23 AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/
RUN ./gradlew build -x test --no-daemon || true
COPY src/ src/
RUN ./gradlew clean build -x test --no-daemon

#STAGE2: RUN
FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
