# 1. faza: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiramo pom.xml (za product-service) i preuzimamo zavisnosti
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiramo izvorni kod i pakujemo .jar
COPY src ./src
RUN mvn clean package -DskipTests

# 2. faza: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiramo generisani .jar iz build faze
COPY --from=build /app/target/*.jar app.jar

# Pokrećemo Product Service
ENTRYPOINT ["java", "-jar", "app.jar"]