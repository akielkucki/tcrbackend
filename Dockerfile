# Use an official OpenJDK runtime as a base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file
COPY target/TCRBackend-1.0.jar app.jar

# Ensure JTE templates are included in the container
COPY target/classes /app/classes

# Expose the port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
