# Use a small official JDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Create data directory for JSON files
RUN mkdir -p /app/data

# Copy the Spring Boot fat jar into the container
COPY target/fintech-0.0.1-SNAPSHOT.jar app.jar

# Set up runtime entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]
