# ---- build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

# Copy the wrapper and POM first so dependency downloads are cached as long as
# pom.xml is unchanged. Without this every source edit re-downloads all of Maven.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src/ src/
# Tests are skipped here: HealthNetApplicationTests is a @SpringBootTest that
# loads the full context, and there is no database inside the build container.
RUN ./mvnw -B clean package -DskipTests

# ---- runtime stage ----
# JRE only, so the final image ships without the compiler and Maven cache.
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -r -u 1001 spring
USER spring

COPY --from=build /build/target/HealthNet-0.0.1-SNAPSHOT.jar app.jar

# Free tiers hand the port to the app via $PORT; 8081 is the local default.
ENV PORT=8081
EXPOSE 8081

# Pure exec form, no shell wrapper: Heroku re-runs the ENTRYPOINT through its own
# `sh -c`, which mangles a nested ["sh","-c","..."] into `java` with no arguments.
# $PORT needs no expansion here — application.properties already resolves it via
# server.port=${PORT:8081}, straight from the environment.
#
# MaxRAMPercentage keeps the heap inside the container limit. Free instances are
# often 512 MB, where the JVM's default sizing would overcommit and get OOM-killed.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-XX:+UseSerialGC", "-jar", "app.jar"]
