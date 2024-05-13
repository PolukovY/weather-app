# Use a base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/*.jar /app/app.jar

# Command to run the application
CMD ["java", "-jar", "app.jar"]
