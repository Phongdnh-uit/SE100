# =========================
# STAGE 1: BUILD
# =========================
FROM gradle:8.10-jdk23 AS builder
WORKDIR /app

# Copy gradle wrapper & config trước để tận dụng cache
COPY gradlew build.gradle settings.gradle ./
COPY gradle/ gradle/

# Copy source
COPY src/ src/

# Build Spring Boot jar
RUN ./gradlew clean bootJar -x test --no-daemon

# =========================
# STAGE 2: RUN
# =========================
FROM eclipse-temurin:23-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
