# Stage 1: Build the artifact
FROM gradle:8.9.0-jdk21-alpine AS build
# Set the working directory to the project directory
WORKDIR /home/gradle/src
COPY ./ ./
# Build the project
RUN ./gradlew build -x test --no-daemon
# Stage 2: Create the production-ready image (jre image not available for java 21:) We are using jdk instead)
FROM openjdk:21-jdk-slim
EXPOSE 8080

# Create a group and user
RUN groupadd -r spring && useradd -r -g spring spring

# Create the app directory and set the permissions
RUN mkdir /app && chown -R spring:spring /app

# Copy the built artifact from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar /app/application.jar
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring

WORKDIR /app
ENTRYPOINT ["java", "-jar", "application.jar"]
