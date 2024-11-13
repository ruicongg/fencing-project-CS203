# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar ./app.jar

# Create a non-root user to run the application
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser

# Expose the port the app runs on
EXPOSE 8080

# Run the application
# CMD ["java", "-jar", "app.jar"]
# trying to resolve health check failing when connecting to RDS
CMD ["java", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]
