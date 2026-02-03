# 1. faza: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiranje POM-a i preuzimanje zavisnosti
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiranje izvornog koda i pakovanje
COPY src ./src
RUN mvn clean package -DskipTests

# 2. faza: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiranje generisanog jar fajla
COPY --from=build /app/target/*.jar app.jar

# Pokretanje Sales servisa na portu 8083
ENTRYPOINT ["java", "-jar", "app.jar"]