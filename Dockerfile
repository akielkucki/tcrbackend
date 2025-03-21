# Use an official OpenJDK runtime as a base image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file
COPY target/TCRBackend-1.0.jar app.jar

# Create uploads directory for runtime uploads
RUN mkdir -p /app/uploads

# Expose the port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]