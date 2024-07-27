# Stage 1: Build the artifact
FROM maven:3.9.8-amazoncorretto-21 AS build

# Set the working directory to the project directory
WORKDIR /home/maven/src

# Copy the Maven wrapper and build files first for better caching
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn

# Download dependencies without running tests
RUN ./mvnw dependency:go-offline -B

# Copy the remaining project files
COPY ./src ./src

# Build the project
RUN ./mvnw package -DskipTests

# Stage 2: Create the production-ready image
FROM openjdk:21-jdk-slim
EXPOSE 8080

# Create a group and user
RUN groupadd -r spring && useradd -r -g spring spring

# Create the app directory and set the permissions
RUN mkdir /app && chown -R spring:spring /app

# Copy the built artifact from the build stage
COPY --from=build /home/maven/src/target/*.jar /app/application.jar
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring

WORKDIR /app
ENTRYPOINT ["java", "-jar", "application.jar"]
