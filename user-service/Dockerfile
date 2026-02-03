# 1. faza: Build (kompajliranje aplikacije)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiramo pom.xml i preuzimamo zavisnosti
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiramo izvorni kod i pakujemo aplikaciju
COPY src ./src
RUN mvn clean package -DskipTests

# 2. faza: Runtime (pokretanje aplikacije)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiramo samo finalni .jar fajl iz prve faze
COPY --from=build /app/target/*.jar app.jar

# Pokrećemo servis
ENTRYPOINT ["java", "-jar", "app.jar"]