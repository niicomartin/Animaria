# Dockerfile preparado para Render/Railway/VPS
# Construye la aplicación con Maven y luego ejecuta el JAR con Java 8.

FROM maven:3.8.6-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:8-jre-jammy
WORKDIR /app
COPY --from=build /app/target/PetShop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
