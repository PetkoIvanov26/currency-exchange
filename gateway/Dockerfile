# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/gateway.jar /app/gateway.jar

# Expose the port the app will run on
EXPOSE 8081

# Run the JAR file
ENTRYPOINT ["java", "-jar", "gateway.jar"]