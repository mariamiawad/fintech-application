FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Create data directory for JSON files
RUN mkdir -p /app/data

# Copy the Spring Boot fat jar into the container
COPY target/fintech-0.0.1-SNAPSHOT.jar app.jar
# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
