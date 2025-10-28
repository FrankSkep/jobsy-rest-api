# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/jobsy-rest-api-1.0.jar jobsy.jar

EXPOSE 8080

# Java runtime options for optimized performance
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-XX:+UseCompressedOops", "-jar", "jobsy.jar"]