# Dockerfile for employee-data (App2)

# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# 3. Copy the built JAR into the container
COPY target/employee-data-0.0.1-SNAPSHOT.jar /app/employee-data.jar

# Expose the port that App2 will run on
EXPOSE 9091

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "employee-data.jar"]
