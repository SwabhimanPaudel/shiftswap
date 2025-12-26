# Use a specific OpenJDK image as the base
FROM openjdk:17-jdk-slim AS build
# Set the working directory inside the container
WORKDIR /app
# Copy the Maven/Gradle files to enable building
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
# Use Maven wrapper to build the project into a JAR file
RUN ./mvnw install -DskipTests
# Define the final runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
# Command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]