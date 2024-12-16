# Dockerfile for employee-api (App1)

# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# 3. Copy the actual built JAR into the container
COPY target/employee-api-0.0.1-SNAPSHOT.jar /app/employee-api.jar

# Expose the port that App1 will run on
EXPOSE 9090

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "employee-api.jar"]
