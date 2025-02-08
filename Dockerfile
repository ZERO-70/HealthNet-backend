# Use an OpenJDK image
FROM openjdk:21-jdk-slim

# Copy the Maven project files into the container
COPY . .

# Build the project inside the container
RUN ./mvnw clean install

# Copy the built JAR file into the container
COPY target/HealthNet-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
