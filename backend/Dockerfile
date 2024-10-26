# Build stage
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Run stage
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar ./app.jar

# Create a non-root user to run the application
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser

# Expose the port the app runs on
EXPOSE 8080

# Set any environment variables if needed
# ENV SPRING_PROFILES_ACTIVE=production

# # Health check
# HEALTHCHECK --interval=30s --timeout=3s \
#   CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["java", "-jar", "app.jar"]
