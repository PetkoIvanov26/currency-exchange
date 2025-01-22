FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copy the entire project context
COPY . /app

# Make gradlew executable (relative path - preferred)
RUN chmod +x gradlew

# Set the working directory to the gateway module (AFTER chmod)
WORKDIR /app/excahnge-rate-scheduler

# Build the gateway module (relative path)
RUN ../gradlew build -x test

# Production stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy only the gateway JAR
COPY --from=build /app/excahnge-rate-scheduler/build/libs/*.jar /app/excahnge-rate-scheduler.jar

EXPOSE 8081
CMD ["java", "-jar", "/app/gateway.jar"]