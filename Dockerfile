# ===== STAGE 1: build =====
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests


# ===== STAGE 2: runtime =====
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
