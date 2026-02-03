# 1. faza: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiranje POM-a i keširanje zavisnosti
COPY pom.xml .
RUN mvn dependency:go-offline

# Pakovanje aplikacije
COPY src ./src
RUN mvn clean package -DskipTests

# 2. faza: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiranje jar fajla
COPY --from=build /app/target/*.jar app.jar

# Servis će raditi na portu 8084
ENTRYPOINT ["java", "-jar", "app.jar"]