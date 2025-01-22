# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the correct JAR file into the container (update to match the correct path)
COPY build/libs/exchange-rate-scheduler.jar /app/scheduler.jar

# Expose the port the app will run on
EXPOSE 8082

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/scheduler.jar"]