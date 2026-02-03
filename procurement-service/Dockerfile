# 1. faza: Build (Maven i JDK 17)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiramo definiciju projekta
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiramo kod i pravimo izvršni jar
COPY src ./src
RUN mvn clean package -DskipTests

# 2. faza: Runtime (Samo JRE za minimalnu veličinu slike)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiramo build-ovan jar fajl
COPY --from=build /app/target/*.jar app.jar

# Pokrećemo servis
ENTRYPOINT ["java", "-jar", "app.jar"]